package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import java.time.LocalDate

data class TodoCountResponse(
    var deadline: LocalDate? = null,
    var count: Long? = null
)
