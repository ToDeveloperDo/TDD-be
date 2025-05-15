package io.junseok.todeveloperdo.client.openai.config

import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.AUTHORIZATION
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumAiRequest
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumAiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "TddAiService",
    url = "https://api.openai.com/v1/chat/completions",
    configuration = [OpenChatAiConfig::class]
)
interface OpenChatClient {

    @PostMapping(headers = ["Content-Type=application/json"])
    fun getCurriculumResponse(
        @RequestHeader(AUTHORIZATION) secretKey: String,
        @RequestBody request: CurriculumAiRequest,
    ): CurriculumAiResponse
}