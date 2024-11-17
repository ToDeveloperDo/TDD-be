package io.junseok.todeveloperdo.oauth.git.config

import feign.Client
import feign.Logger
import feign.codec.Decoder
import feign.codec.Encoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    fun feignDecoder(): Decoder {
        return JacksonDecoder()
    }

    @Bean
    fun feignEncoder(): Encoder {
        return JacksonEncoder()
    }

    @Bean
    fun client(): Client {
        return OkHttpClient()
    }
}