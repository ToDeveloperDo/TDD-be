package io.junseok.todeveloperdo.client.openai.dto.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Message @JsonCreator constructor(
    @JsonProperty("content") val content: String,
    @JsonProperty("role") val role: String,
) {
    companion object {
        fun String.create() = Message(
            content = this,
            role = "system"
        )
    }
}
