package io.junseok.todeveloperdo.oauth.git.config

import feign.Logger
import feign.RequestInterceptor
import feign.codec.Decoder
import feign.codec.Encoder
import feign.codec.StringDecoder
import feign.form.FormEncoder
import feign.optionals.OptionalDecoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitHubClientConfig() {

    @Value("\${github.access-token}")
    private lateinit var accessToken: String
    @Bean
    fun feignFormEncoder(): Encoder {
        return FormEncoder()
    }

    @Bean
    fun feignDecoder(): Decoder {
        return OptionalDecoder(StringDecoder())
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template ->
            val hardcodedAccessToken = "Bearer $accessToken"  // 하드코딩된 토큰 설정
            template.header("Authorization", hardcodedAccessToken)
        }
    }
}
