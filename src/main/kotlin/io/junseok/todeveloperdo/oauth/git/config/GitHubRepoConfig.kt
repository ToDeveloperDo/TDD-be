package io.junseok.todeveloperdo.oauth.git.config

import feign.Logger
import feign.codec.Decoder
import feign.codec.Encoder
import feign.form.spring.SpringFormEncoder
import feign.jackson.JacksonDecoder
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringEncoder
import org.springframework.cloud.openfeign.support.SpringMvcContract
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitHubRepoConfig(private val messageConverters: ObjectFactory<HttpMessageConverters>) {

    @Bean
    fun feignEncoder(): Encoder {
        return SpringFormEncoder(SpringEncoder(messageConverters))
    }

    @Bean
    fun feignDecoder(): Decoder {
        return JacksonDecoder()
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    fun feignContract(): SpringMvcContract {
        return SpringMvcContract()
    }
}
