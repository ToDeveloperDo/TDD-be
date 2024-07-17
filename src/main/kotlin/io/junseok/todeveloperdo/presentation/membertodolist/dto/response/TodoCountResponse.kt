package io.junseok.todeveloperdo.presentation.membertodolist.dto.response

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDate

data class TodoCountResponse @QueryProjection constructor(
    val deadline: LocalDate, val count: Long
)
