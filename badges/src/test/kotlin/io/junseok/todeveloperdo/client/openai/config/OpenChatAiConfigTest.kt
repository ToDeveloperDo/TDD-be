package io.junseok.todeveloperdo.client.openai.config

import feign.jackson.JacksonEncoder
import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.AUTHORIZATION
import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.SECRET_KEY_PREFIX
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class OpenChatAiConfigTest : FunSpec({
    val config = OpenChatAiConfig()

    test("feignEncoder는 JacksonEncoder를 반환해야 한다") {
        config.feignEncoder().shouldBeInstanceOf<JacksonEncoder>()
    }
    test("AUTHORIZATION 상수는 'Authorization'이다") {
        AUTHORIZATION shouldBe "Authorization"
    }

    test("SECRET_KEY_PREFIX 상수는 'Bearer '이다") {
        SECRET_KEY_PREFIX shouldBe "Bearer "
    }
})
