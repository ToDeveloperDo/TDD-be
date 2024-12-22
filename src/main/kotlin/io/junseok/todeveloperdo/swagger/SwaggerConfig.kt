package io.junseok.todeveloperdo.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val apiKey: SecurityScheme = SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")
        
        val securityRequirement: SecurityRequirement = SecurityRequirement()
            .addList("Bearer Token")

        return OpenAPI()
            .components(Components().addSecuritySchemes("Bearer Token",apiKey))
            .info(
                Info()
                    .title("TDD-API")
                    .version("1.0.0")
                    .description("개발자를 위한 ToDoList")
            )
            .addSecurityItem(securityRequirement)
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