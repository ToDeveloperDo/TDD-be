package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import java.time.LocalDateTime

data class TodoResponse(
    val content:String,
    val memo:String?,
    val tag: String,
    val deadline: LocalDateTime
)

fun MemberTodoList.toTodoResponse() = TodoResponse(
    content = this.content,
    memo = this.memo,
    tag = this.tag,
    deadline = this.deadline
)
