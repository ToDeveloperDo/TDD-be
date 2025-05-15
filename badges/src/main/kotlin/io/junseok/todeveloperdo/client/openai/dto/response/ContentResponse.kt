package io.junseok.todeveloperdo.client.openai.dto.response

data class ContentResponse(
    val content: String,
    val isChecked: Boolean
)