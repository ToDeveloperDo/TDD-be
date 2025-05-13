package io.junseok.todeveloperdo.oauth.git.config

import feign.Logger
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FeignConfigTest : FunSpec({
    val config = FeignConfig()

    test("feignEncoder는 JacksonEncoder를 반환해야 한다") {
        config.feignEncoder().shouldBeInstanceOf<JacksonEncoder>()
    }

    test("feignDecoder는 JacksonDecoder를 반환해야 한다") {
        config.feignDecoder().shouldBeInstanceOf<JacksonDecoder>()
    }

    test("feignLoggerLevel은 FULL이어야 한다") {
        config.feignLoggerLevel() shouldBe Logger.Level.FULL
    }

    test("client는 OkHttpClient를 반환해야 한다") {
        config.client().shouldBeInstanceOf<OkHttpClient>()
    }
})
