package io.junseok.todeveloperdo.swagger

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.security.SecurityScheme

class SwaggerConfigTest : FunSpec({

    val config = SwaggerConfig()

    test("customOpenAPI()는 Bearer Token 헤더 보안 설정을 포함한다") {
        val openAPI = config.customOpenAPI()

        val securityScheme = openAPI.components.securitySchemes["Bearer Token"]
        securityScheme?.type shouldBe SecurityScheme.Type.APIKEY
        securityScheme?.`in` shouldBe SecurityScheme.In.HEADER
        securityScheme?.name shouldBe "Authorization"

        openAPI.info.title shouldBe "TDD-API"
        openAPI.info.version shouldBe "1.0.0"
        openAPI.info.description shouldBe "개발자를 위한 ToDoList"

        openAPI.servers.map { it.url } shouldBe listOf(
            "https://api.todeveloperdo.shop",
            "https://dev.todeveloperdo.shop",
            "http://localhost:8080"
        )
    }

    test("authApi()는 인증이 필요한 API 그룹이며 로그인 경로 등을 제외한다") {
        val group = config.authApi()
        group.group shouldBe "인증이 필요한 API"
        group.pathsToExclude.toList() shouldBe listOf("/**/login/**", "/api/member/all")
    }

    test("nonAuthApi()는 인증이 필요하지 않은 API 그룹이며 로그인 경로 등을 포함한다") {
        val group = config.nonAuthApi()
        group.group shouldBe "인증이 불필요한 API"
        group.pathsToMatch.toList() shouldBe listOf("/**/login/**", "/api/member/all")
    }
})
