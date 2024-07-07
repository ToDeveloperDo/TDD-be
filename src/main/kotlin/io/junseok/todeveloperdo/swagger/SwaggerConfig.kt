package io.junseok.todeveloperdo.swagger

import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration //스프링 실행시 설정파일 읽어드리기 위한 어노테이
class SwaggerConfig {

    @Bean
    fun publicApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/**")
            .build()
    }
}