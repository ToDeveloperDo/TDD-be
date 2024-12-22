package io.junseok.todeveloperdo.client.openai.dto.request

data class ContentRequest(
    val content: String,
    val isChecked: Boolean
)
