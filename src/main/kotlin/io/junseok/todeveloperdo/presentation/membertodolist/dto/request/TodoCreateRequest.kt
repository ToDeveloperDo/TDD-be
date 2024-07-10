package io.junseok.todeveloperdo.presentation.membertodolist.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class TodoCreateRequest(
    val content:String,
    val memo:String?,
    val tag: String,
    var isShare: Boolean,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val deadline: LocalDate
)