package io.junseok.todeveloperdo.client.openai.config

import feign.RequestInterceptor
import feign.RequestTemplate
import feign.codec.Encoder
import feign.jackson.JacksonEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenChatAiConfig(
    @Value("\${openai.api-key}") private val secretKey: String
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { requestTemplate: RequestTemplate ->
            requestTemplate.header(
                CONTENT_TYPE_KEY,
                CONTENT_TYPE_VALUE
            )
            requestTemplate.header(SECRET_KEY_KEY, "$SECRET_KEY_PREFIX$secretKey")
        }
    }

    @Bean
    fun feignEncoder(): Encoder {
        return JacksonEncoder()
    }

    companion object {
        private const val CONTENT_TYPE_KEY = "Content-Type"
        private const val CONTENT_TYPE_VALUE = "application/json"
        private const val SECRET_KEY_KEY = "Authorization"
        private const val SECRET_KEY_PREFIX = "Bearer "
    }
}
