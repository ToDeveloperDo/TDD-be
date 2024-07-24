package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.*
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_CLOSED
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_OPEN
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueValidator
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoDateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class MemberTodoService(
    private val todoReader: TodoReader,
    private val memberReader: MemberReader,
    private val todoSaver: TodoSaver,
    private val todoCreator: TodoCreator,
    private val todoUpdater: TodoUpdater,
    private val gitIssueService: GitIssueService,
    private val todoValidator: TodoValidator,
    private val todoDeleter: TodoDeleter,
    private val issueValidator: GitHubIssueValidator,
    private val eventProcessor: EventProcessor
) {
    fun createTodoList(
        todoRequest: TodoRequest,
        username: String
    ): Long? {
        val member = memberReader.getMember(username)

        //오늘 할 일이 아닌 경우
        if (LocalDate.now() != todoRequest.deadline) {
            gitIssueService.saveGitIssue(todoRequest, member)
            val memberTodoList = todoCreator.generatorTodo(todoRequest, member)
            return todoSaver.saveTodoList(memberTodoList)
        }
        val issueEventRequest = eventProcessor.createIssue(member, todoRequest)
        val memberTodoList =
            todoCreator.generatorTodo(todoRequest, member, issueEventRequest.issueNumber.get())
        val todoListId = todoSaver.saveTodoList(memberTodoList)
        eventProcessor.createReadMe(member)
        return todoListId
    }

    fun findTodoLists(todoDateRequest: TodoDateRequest, username: String): List<TodoResponse> {
        val member = memberReader.getMember(username)
        return todoReader.bringTodoLists(todoDateRequest.deadline, member)
    }

    fun finishTodoList(todoListId: Long, username: String) {
        val memberTodoList = todoReader.findTodoList(todoListId)
        val member = memberReader.getMember(username)
        todoUpdater.doneTodoList(memberTodoList)
        eventProcessor.closeIssueWithReadMe(member, memberTodoList.issueNumber!!, ISSUE_CLOSED)
    }

    fun modifyTodoList(todoListId: Long, todoRequest: TodoRequest, username: String) {
        val member = memberReader.getMember(username)
        val todoList = todoReader.findTodoList(todoListId)
        todoValidator.isWriter(todoListId, member)
        todoUpdater.update(todoList, todoRequest)
        issueValidator.isExist(todoList)
        eventProcessor.updateIssueWithReadMe(
            member,
            todoList.issueNumber!!,
            todoRequest.toTodoCreate(member)
        )
    }

    fun removeTodoList(todoListId: Long, username: String) {
        val member = memberReader.getMember(username)
        val todoList = todoReader.findTodoList(todoListId)
        todoDeleter.delete(todoList)
        eventProcessor.closeIssueWithReadMe(member, todoList.issueNumber!!, ISSUE_CLOSED)
    }

    @Transactional(readOnly = true)
    fun calculateTodoList(
        todoCountRequest: TodoCountRequest,
        username: String
    ): List<TodoCountResponse> {
        val member = memberReader.getMember(username)
        return todoReader.countByTodoList(todoCountRequest, member)
    }

    fun unFinishedTodoList(todoListId: Long, username: String) {
        val findTodoList = todoReader.findTodoList(todoListId)
        val member = memberReader.getMember(username)
        todoUpdater.proceedTodoList(findTodoList)
        eventProcessor.closeIssueWithReadMe(member, findTodoList.issueNumber!!, ISSUE_OPEN)
    }
}