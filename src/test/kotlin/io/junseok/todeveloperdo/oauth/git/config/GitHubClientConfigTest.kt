package io.junseok.todeveloperdo.oauth.git.config

import feign.Logger
import feign.form.FormEncoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import feign.optionals.OptionalDecoder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class GitHubClientConfigTest : FunSpec({
    val config = GitHubClientConfig()

    test("feignFormEncoder는 FormEncoder를 반환해야 한다") {
        config.feignFormEncoder().shouldBeInstanceOf<FormEncoder>()
    }

    test("feignDecoder는 OptionalDecoder를 반환해야 한다") {
        config.feignDecoder().shouldBeInstanceOf<OptionalDecoder>()
    }

    test("feignLoggerLevel은 FULL이어야 한다") {
        config.feignLoggerLevel() shouldBe Logger.Level.FULL
    }

})
