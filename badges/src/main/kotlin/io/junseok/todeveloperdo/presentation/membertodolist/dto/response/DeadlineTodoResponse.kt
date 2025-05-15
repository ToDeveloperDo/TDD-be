package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import java.time.LocalDate

data class DeadlineTodoResponse(
    val deadline: LocalDate,
    val todoResponse: List<TodoResponse>
)
