package io.junseok.todeveloperdo.oauth.apple.config

import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.FormEncoder
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppleConfig {
    @Bean
    fun feignFormEncoder(): Encoder {
        return FormEncoder(JacksonEncoder())
    }

    @Bean
    fun feignDecoder(): Decoder {
        return JacksonDecoder()
    }
}