package io.junseok.todeveloperdo.oauth.git.config

import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.FormEncoder
import feign.optionals.OptionalDecoder
import feign.slf4j.Slf4jLogger
import feign.Logger
import feign.codec.StringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitHubClientConfig {

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
}
