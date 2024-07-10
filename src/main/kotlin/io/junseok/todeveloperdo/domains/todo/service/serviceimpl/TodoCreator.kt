package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import org.springframework.stereotype.Component

@Component
class TodoCreator {
    fun generatorTodo(todoCreateRequest: TodoCreateRequest, issueNumber: Int, member: Member) =
        MemberTodoList(
            content = todoCreateRequest.content,
            memo = todoCreateRequest.memo,
            tag = todoCreateRequest.tag,
            deadline = todoCreateRequest.deadline,
            todoStatus = TodoStatus.PROCEED,
            isShare = todoCreateRequest.isShare,
            issueNumber = issueNumber,
            member = member
        )
}