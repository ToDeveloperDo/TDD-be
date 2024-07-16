package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import java.time.LocalDate

data class TodoCountResponse(val deadline: LocalDate, val count: Long)
