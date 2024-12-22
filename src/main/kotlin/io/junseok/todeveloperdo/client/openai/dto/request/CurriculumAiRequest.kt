package io.junseok.todeveloperdo.client.openai.dto.request


data class CurriculumAiRequest(
    val model: String,
    val messages: List<Message>,
    val response_format: ResponseFormat
){
    companion object {
        fun List<Message>.generateRequest() = CurriculumAiRequest(
            model = "gpt-4o-mini",
            messages = this,
            response_format = ResponseFormat(type = "json_object")
        )
    }
}

data class ResponseFormat(
    val type: String
)