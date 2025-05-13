package io.junseok.todeveloperdo.oauth.git.config

import feign.Logger
import feign.form.spring.SpringFormEncoder
import feign.jackson.JacksonDecoder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringMvcContract

class GitHubRepoConfigTest : FunSpec({
    val messageConverters = mockk<ObjectFactory<HttpMessageConverters>>()
    val config = GitHubRepoConfig(messageConverters)

    test("feignEncoder는 JacksonEncoder를 반환해야 한다") {
        config.feignEncoder().shouldBeInstanceOf<SpringFormEncoder>()
    }

    test("feignDecoder는 JacksonDecoder를 반환해야 한다") {
        config.feignDecoder().shouldBeInstanceOf<JacksonDecoder>()
    }

    test("feignLoggerLevel은 FULL이어야 한다") {
        config.feignLoggerLevel() shouldBe Logger.Level.FULL
    }

    test("feignContract는 SpringMvcContract 반환해야 한다") {
        config.feignContract().shouldBeInstanceOf<SpringMvcContract>()
    }
})
