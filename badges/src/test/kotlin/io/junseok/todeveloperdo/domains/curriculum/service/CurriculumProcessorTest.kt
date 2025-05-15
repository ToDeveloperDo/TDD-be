package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.config.OpenChatClient
import io.junseok.todeveloperdo.client.openai.dto.request.Choice
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumAiRequest.Companion.generateRequest
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumAiResponse
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest
import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest.Companion.toPrompt
import io.junseok.todeveloperdo.client.openai.dto.request.Message
import io.junseok.todeveloperdo.client.openai.dto.request.Message.Companion.create
import io.junseok.todeveloperdo.global.generateSecretToken
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CurriculumProcessorTest : FunSpec({
    val openChatClient = mockk<OpenChatClient>()
    val secretKey = "sk-test"
    val curriculumProcessor = CurriculumProcessor(
        openChatClient, secretKey
    )

    test("OpenAI 커리큘럼 요청 시 정상 응답을 반환한다") {
        val prompt = createCurriculumRequest().toPrompt()
        val messages = listOf(prompt.create())
        val request = messages.generateRequest()
        val response = createCurriculumAiResponse()
        val token = secretKey.generateSecretToken()

        every {
            openChatClient.getCurriculumResponse(
                token,
                request
            )
        } returns response

        val result = curriculumProcessor.recommendCurriculum(createCurriculumRequest())

        result shouldBe response.result
        verify(exactly = 1) {
            openChatClient.getCurriculumResponse(
                token,
                request
            )
        }
    }

    test("OpenAI 호출 실패 시 예외를 던진다") {
        val prompt = createCurriculumRequest().toPrompt()
        val request = listOf(prompt.create()).generateRequest()
        val token = secretKey.generateSecretToken()

        every {
            openChatClient.getCurriculumResponse(
                token,
                request
            )
        } throws RuntimeException("OpenAI down")

        throwsWith<RuntimeException>({
            curriculumProcessor.recommendCurriculum(createCurriculumRequest())
        },
            { ex -> RuntimeException(ex) }
        )
    }

})

fun createCurriculumRequest() = CurriculumRequest(
    position = "position",
    stack = "stack",
    experienceLevel = "experienceLevel",
    targetPeriod = 1
)

fun createCurriculumAiResponse() = CurriculumAiResponse(
    choices = listOf(Choice(createMessage()))
)

fun createMessage() = Message(
    content = "AI Content",
    role = "system"
)