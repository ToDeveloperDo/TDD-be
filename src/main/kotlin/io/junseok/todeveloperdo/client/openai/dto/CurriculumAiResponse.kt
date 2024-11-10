package io.junseok.todeveloperdo.client.openai.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CurriculumAiResponse @JsonCreator constructor(
    @JsonProperty("choices") val choices: List<Choice>,
) {
    val result: String
        get() = choices[0].message.content
}

data class Choice @JsonCreator constructor(
    @JsonProperty("message") val message: Message,
)
