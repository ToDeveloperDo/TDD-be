package io.junseok.todeveloperdo.client.openai.dto.request

data class RegisterRequest(
    val weekTitle: String,
    val objective: String,
    val contentRequests: List<ContentRequest>
)
