package io.junseok.todeveloperdo.client.openai.dto.request


data class CurriculumAiRequest(
    val model: String,
    val messages: List<Message>,
    val response_format: ResponseFormat
){
    companion object {
        private const val AI_MODEL = "gpt-4o-mini"
        private val RESPONSE_FORMAT = ResponseFormat("json_object")

        fun List<Message>.generateRequest() = CurriculumAiRequest(
            model = AI_MODEL,
            messages = this,
            response_format = RESPONSE_FORMAT
        )
    }
}

data class ResponseFormat(
    val type: String
)