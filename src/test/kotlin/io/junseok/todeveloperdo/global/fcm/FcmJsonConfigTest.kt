package io.junseok.todeveloperdo.global.fcm

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.junseok.todeveloperdo.exception.ErrorCode.FAILED_JSON_PROCESSING
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class FcmJsonConfigTest : FunSpec({

    test("toJson()을 호출하면 모든 필드는 JSON형식으로 반환된다.") {
        val objectMapper = mockk<ObjectMapper>()
        val config = FcmJsonConfig(
            projectId = "test-project",
            privateKeyId = "test-private-key-id",
            privateKey = "-----BEGIN PRIVATE KEY-----\\ntest-key\\n-----END PRIVATE KEY-----",
            clientEmail = "test@test.com",
            clientId = "1234567890",
            type = "service_account",
            authUrl = "https://accounts.google.com/o/oauth2/auth",
            tokenUrl = "https://oauth2.googleapis.com/token",
            authProviderX509CertUrl = "https://www.googleapis.com/oauth2/v1/certs",
            clientX509CertUrl = "https://www.googleapis.com/robot/v1/metadata/x509/test@test.com",
            universeDomain = "googleapis.com",
            objectMapper = objectMapper
        )

        every { objectMapper.writeValueAsString(any()) } returns """{"project_id":"test-project"}"""
        val json = config.toJson()

        json shouldNotBe null
        json shouldBe """{"project_id":"test-project"}"""
    }

    test("toJson()을 호출했을때 JsonProcessingException이 발생하면  FAILED_JSON_PROCESSING이 반환된다.") {
        val objectMapper = mockk<ObjectMapper>()
        every { objectMapper.writeValueAsString(any()) } throws JsonMappingException("boom")
        val config = FcmJsonConfig(
            objectMapper = objectMapper,
            projectId = "p",
            privateKeyId = "k",
            privateKey = "bad-key",
            clientEmail = "e",
            clientId = "c",
            type = "t",
            authUrl = "a",
            tokenUrl = "t",
            authProviderX509CertUrl = "a",
            clientX509CertUrl = "c",
            universeDomain = "u"
        )

        throwsWith<ToDeveloperDoException>(
            {
                config.toJson()
            },
            {
                ex -> ex.errorCode shouldBe FAILED_JSON_PROCESSING
            }
        )
    }
})
