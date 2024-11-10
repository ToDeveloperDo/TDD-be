package io.junseok.todeveloperdo.client.openai.service

import io.junseok.todeveloperdo.client.openai.config.OpenChatClient
import io.junseok.todeveloperdo.client.openai.dto.CurriculumAiRequest.Companion.generateRequest
import io.junseok.todeveloperdo.client.openai.dto.CurriculumRequest
import io.junseok.todeveloperdo.client.openai.dto.CurriculumRequest.Companion.toPrompt
import io.junseok.todeveloperdo.client.openai.dto.Message.Companion.create
import org.springframework.stereotype.Component

@Component
class CurriculumProcessor(
    private val openChatClient: OpenChatClient,
) {
    fun recommendCurriculum(curriculumRequest: CurriculumRequest): String {
        val prompt = curriculumRequest.toPrompt()
        val messages = listOf(prompt.create())
        val request = messages.generateRequest()
        val curriculumAiResponse = openChatClient.getCurriculumResponse(request)
        return curriculumAiResponse.result
    }
}