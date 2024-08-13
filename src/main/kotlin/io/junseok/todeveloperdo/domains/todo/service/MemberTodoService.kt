package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.aop.annotation.close.EventHandler
import io.junseok.todeveloperdo.aop.annotation.create.CreateEvent
import io.junseok.todeveloperdo.aop.annotation.create.ReadMeCreate
import io.junseok.todeveloperdo.aop.annotation.delete.DeleteEventHandler
import io.junseok.todeveloperdo.aop.annotation.update.UpdateEvent
import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.*
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueValidator
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoDateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
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
    private val issueValidator: GitHubIssueValidator,
) {
    @CreateEvent
    @ReadMeCreate
    fun createTodoList(
        todoRequest: TodoRequest,
        username: String,
        issueEventRequest: IssueEventRequest? = null
    ): Long? {
        val member = memberReader.getMember(username)

        //오늘 할 일이 아닌 경우
        if (LocalDate.now() != todoRequest.deadline) {
            gitIssueService.saveGitIssue(todoRequest, member)
            val memberTodoList = todoCreator.generatorTodo(todoRequest, member)
            return todoSaver.saveTodoList(memberTodoList)
        }

        val issueNumber = issueEventRequest?.issueNumber?.get()
            ?: throw IllegalStateException("IssueEventRequest cannot be null")

        val memberTodoList =
            todoCreator.generatorTodo(todoRequest, member, issueNumber)
        val todoListId = todoSaver.saveTodoList(memberTodoList)
        return todoListId
    }

    fun findTodoLists(todoDateRequest: TodoDateRequest, username: String): List<TodoResponse> {
        val member = memberReader.getMember(username)
        return todoReader.bringTodoLists(todoDateRequest.deadline, member)
    }

    @EventHandler
    fun finishTodoList(todoListId: Long, username: String,state: String) {
        val memberTodoList = todoReader.findTodoList(todoListId)
        todoUpdater.doneTodoList(memberTodoList)
    }

    @UpdateEvent
    fun modifyTodoList(todoListId: Long, todoRequest: TodoRequest, username: String) {
        val member = memberReader.getMember(username)
        val todoList = todoReader.findTodoList(todoListId)
        todoValidator.isWriter(todoListId, member)
        todoUpdater.update(todoList, todoRequest)
        issueValidator.isExist(todoList)
    }
    @DeleteEventHandler
    fun removeTodoList(todoListId: Long, username: String, state: String) {}


    @Transactional(readOnly = true)
    fun calculateTodoList(
        todoCountRequest: TodoCountRequest,
        username: String
    ): List<TodoCountResponse> {
        val member = memberReader.getMember(username)
        return todoReader.countByTodoList(todoCountRequest, member)
    }

    @EventHandler
    fun unFinishedTodoList(todoListId: Long, username: String, state: String) {
        val findTodoList = todoReader.findTodoList(todoListId)
        todoUpdater.proceedTodoList(findTodoList)
    }
}