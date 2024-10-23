package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.aop.annotation.close.EventHandler
import io.junseok.todeveloperdo.aop.annotation.create.CreateEvent
import io.junseok.todeveloperdo.aop.annotation.create.ReadMeCreate
import io.junseok.todeveloperdo.aop.annotation.delete.DeleteEventHandler
import io.junseok.todeveloperdo.aop.annotation.update.UpdateEvent
import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueUpdater
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.*
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
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
    private val gitIssueUpdater: GitIssueUpdater,
) {
    @CreateEvent
    @ReadMeCreate
    fun createTodoList(
        todoRequest: TodoRequest,
        username: String,
        issueEventRequest: IssueEventRequest? = null,
    ): Long? {
        val member = memberReader.getMember(username)

        val memberTodoList = todoCreator.generatorTodo(
            todoRequest,
            member,
            issueEventRequest?.issueNumber?.get()
                ?.takeIf { LocalDate.now() == todoRequest.deadline }
        )

        val saveTodoList = todoSaver.saveTodoList(memberTodoList)
        gitIssueService.saveGitIssue(todoRequest, member, memberTodoList)
        return saveTodoList
    }

    // 할 일 찾기
    fun findTodoLists(todoDateRequest: TodoDateRequest, username: String): List<TodoResponse> {
        val member = memberReader.getMember(username)
        return todoReader.bringTodoLists(todoDateRequest.deadline, member)
    }

    // 완료한 한 일 체크 NOTE
    @EventHandler
    fun finishTodoList(todoListId: Long, username: String, state: String) {
        val memberTodoList = todoReader.findTodoList(todoListId)
        todoUpdater.doneTodoList(memberTodoList)
    }

    // 할 일 수정 NOTE
    @UpdateEvent
    fun modifyTodoList(todoListId: Long, todoRequest: TodoRequest, username: String) {
        val member = memberReader.getMember(username)
        val todoList = todoReader.findTodoList(todoListId)
        todoValidator.isWriter(todoListId, member)
        todoUpdater.update(todoList, todoRequest)
        // 오늘이 아닌 경우
        //if (LocalDate.now() != todoRequest.deadline) {
            gitIssueUpdater.update(member, todoList, todoRequest)
       // }
    }

    //할 일 삭제 NOTE
    @DeleteEventHandler
    fun removeTodoList(todoListId: Long, username: String, state: String) { }


    @Transactional(readOnly = true)
    fun calculateTodoList(
        todoCountRequest: TodoCountRequest,
        username: String,
    ): List<TodoCountResponse> {
        val member = memberReader.getMember(username)
        return todoReader.countByTodoList(todoCountRequest, member)
    }


    // 다시 미 완료로 변환
    @EventHandler
    fun unFinishedTodoList(todoListId: Long, username: String, state: String) {
        val findTodoList = todoReader.findTodoList(todoListId)
        todoUpdater.proceedTodoList(findTodoList)
    }
}