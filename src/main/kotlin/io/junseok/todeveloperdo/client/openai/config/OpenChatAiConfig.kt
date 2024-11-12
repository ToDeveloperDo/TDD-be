package io.junseok.todeveloperdo.client.openai.config

import feign.codec.Encoder
import feign.jackson.JacksonEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenChatAiConfig {

    @Bean
    fun feignEncoder(): Encoder {
        return JacksonEncoder()
    }

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val SECRET_KEY_PREFIX = "Bearer "
    }
}
