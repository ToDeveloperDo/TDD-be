package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import java.time.LocalDate

data class TodoResponse(
    val todoListId: Long,
    val content:String,
    val memo:String?,
    val tag: String,
    val deadline: LocalDate
)

fun MemberTodoList.toTodoResponse() = TodoResponse(
    todoListId = this.todoListId!!,
    content = this.content,
    memo = this.memo,
    tag = this.tag,
    deadline = this.deadline
)
