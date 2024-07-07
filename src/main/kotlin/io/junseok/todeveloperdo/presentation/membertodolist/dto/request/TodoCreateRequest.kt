package io.junseok.todeveloperdo.presentation.membertodolist.dto.request

import java.time.LocalDateTime

data class TodoCreateRequest(
    val content:String,
    val memo:String?,
    val tag: String,
    var isShare: Boolean,
    val deadline: LocalDateTime
) {
}