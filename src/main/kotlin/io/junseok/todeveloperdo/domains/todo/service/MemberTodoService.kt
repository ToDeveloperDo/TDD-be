package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.toTodoResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberTodoService(
    private val memberRepository: MemberRepository,
    private val todoListRepository: TodoListRepository
) {

    @Transactional
    fun createTodoList(
        todoCreateRequest: TodoCreateRequest,
        username: String
    ): Long? {
        val member = (memberRepository.findByUsername(username)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER })

        val memberTodoList = MemberTodoList(
            content = todoCreateRequest.content,
            memo = todoCreateRequest.memo,
            tag = todoCreateRequest.tag,
            deadline = todoCreateRequest.deadline,
            todoStatus = TodoStatus.PROCEED,
            isShare = todoCreateRequest.isShare,
            member = member
        )
        return todoListRepository.save(memberTodoList).todoListId
    }

    @Transactional(readOnly = true)
    fun findTodoList(todoSearchRequest: TodoSearchRequest, username: String) =
        todoListRepository.findAllByDeadline(todoSearchRequest.deadline)
            .map { it.toTodoResponse() }

    @Transactional
    fun finishTodoList(todoListId: Long) {
        val memberTodoList = (todoListRepository.findByIdOrNull(todoListId)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_TODOLIST })
        memberTodoList.updateTodoStatus()
    }

}