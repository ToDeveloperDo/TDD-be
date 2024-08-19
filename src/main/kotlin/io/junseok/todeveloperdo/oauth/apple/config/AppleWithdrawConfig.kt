package io.junseok.todeveloperdo.oauth.apple.config

import feign.codec.Encoder
import feign.form.FormEncoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppleWithdrawConfig {
    @Bean
    fun feignFormEncoder(): Encoder {
        return FormEncoder()
    }
}