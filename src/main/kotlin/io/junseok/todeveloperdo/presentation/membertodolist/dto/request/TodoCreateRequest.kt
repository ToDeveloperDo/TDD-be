package io.junseok.todeveloperdo.presentation.membertodolist.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class TodoCreateRequest(
    val content:String,
    val memo:String?,
    val tag: String,
    var isShare: Boolean,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val deadline: LocalDateTime
) {
}