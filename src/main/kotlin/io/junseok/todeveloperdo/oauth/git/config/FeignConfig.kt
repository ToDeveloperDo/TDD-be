package io.junseok.todeveloperdo.oauth.git.config

import feign.Client
import feign.okhttp.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun feignLoggerLevel(): feign.Logger.Level {
        return feign.Logger.Level.FULL
    }

    @Bean
    fun feignDecoder(): feign.codec.Decoder {
        return feign.jackson.JacksonDecoder()
    }

    @Bean
    fun feignEncoder(): feign.codec.Encoder {
        return feign.jackson.JacksonEncoder()
    }

    @Bean
    fun client(): Client {
        return OkHttpClient()
    }
}