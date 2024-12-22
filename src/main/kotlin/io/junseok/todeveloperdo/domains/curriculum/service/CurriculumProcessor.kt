package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.config.OpenChatClient
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumAiRequest.Companion.generateRequest
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest.Companion.toPrompt
import io.junseok.todeveloperdo.client.openai.dto.request.Message.Companion.create
import io.junseok.todeveloperdo.global.generateSecretToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CurriculumProcessor(
    private val openChatClient: OpenChatClient,
    @Value("\${openai.api-key}")
    private val secretKey: String
) {
    fun recommendCurriculum(
        curriculumRequest: CurriculumRequest
    ): String {
        val prompt = curriculumRequest.toPrompt()
        val messages = listOf(prompt.create())
        val request = messages.generateRequest()
        val curriculumAiResponse =
            openChatClient.getCurriculumResponse(secretKey.generateSecretToken(),request)
        return curriculumAiResponse.result
    }
}