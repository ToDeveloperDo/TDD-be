package io.junseok.todeveloperdo.swagger

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration //스프링 실행시 설정파일 읽어드리기 위한 어노테이
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("TDD-API")
                    .version("1.0.0")
                    .description("개발자를 위한 ToDoList")
            )
            .servers(
                listOf(
                    Server().url("https://api.todeveloperdo.shop"),
                    Server().url("https://dev.todeveloperdo.shop"),
                    Server().url("http://localhost:8080")
                )
            )
    }

    @Bean
    fun authApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("인증이 필요한 API")
            .pathsToExclude("/**/login/**", "/api/member/all")
            .build()
    }

    @Bean
    fun nonAuthApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("인증이 불필요한 API")
            .pathsToMatch("/**/login/**", "/api/member/all")
            .build()
    }
}