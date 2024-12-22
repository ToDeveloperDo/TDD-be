package io.junseok.todeveloperdo.client.openai.dto.request

data class RegisterRequests(
    val registerRequest: List<RegisterRequest>,
    val curriculumRequest: CurriculumRequest,
)
