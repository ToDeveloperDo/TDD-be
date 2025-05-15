package io.junseok.todeveloperdo.client.openai.dto.response

data class CurriculumResponse(
    val weekTitle: String,
    val objective: String,
    val contentRequests: List<ContentResponse>
)
