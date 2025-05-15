package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component

@Component
class TodoCreator {
    fun generatorTodo(
        todoRequest: TodoRequest,
        member: Member,
        issueNumber: Int? = null
    ) = MemberTodoList(
        content = todoRequest.content,
        memo = todoRequest.memo,
        tag = todoRequest.tag,
        deadline = todoRequest.deadline,
        todoStatus = TodoStatus.PROCEED,
        issueNumber = issueNumber,
        member = member
    )
}