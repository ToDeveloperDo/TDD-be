package io.junseok.todeveloperdo.client.openai.config

import io.junseok.todeveloperdo.client.openai.dto.CurriculumAiRequest
import io.junseok.todeveloperdo.client.openai.dto.CurriculumAiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(
    name = "TddAiService",
    url = "https://api.openai.com/v1/chat/completions",
    configuration = [OpenChatAiConfig::class]
)
interface OpenChatClient {

    @PostMapping(headers = ["Content-Type=application/json"])
    fun getCurriculumResponse(
        request: CurriculumAiRequest,
    ): CurriculumAiResponse
}