package io.junseok.todeveloperdo.oauth.apple.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.oauth.apple.dto.request.RefreshTokenRequest
import io.junseok.todeveloperdo.oauth.apple.dto.response.IdTokenResponse
import io.junseok.todeveloperdo.oauth.apple.dto.response.TokenResponse
import io.junseok.todeveloperdo.oauth.apple.service.AppleLoginService
import io.junseok.todeveloperdo.oauth.apple.service.createIdTokenResponse
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(AppleLoginController::class)
@AutoConfigureRestDocs
class AppleLoginControllerTest : BehaviorSpec({
    val appleLoginService = mockk<AppleLoginService>()
    val appleLoginController = AppleLoginController(appleLoginService)
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(appleLoginController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
            .registerModules(JavaTimeModule())
        restDocumentation.beforeTest(javaClass, "AppleLoginController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("사용자가 애플 로그인을 하려고 할 때") {
        val tokenResponse = createAppleTokenResponse()
        val code = "code"
        val clientToken = "clientToken"
        every { appleLoginService.processAppleOAuth(code, clientToken) } returns tokenResponse

        When("POST $APPLE_LOGIN/apple로 요청을 하면") {
            Then("로그인이 정상적으로 진행되어야한다.") {
                val mvcResult = mockMvc.perform(
                    post("$APPLE_LOGIN/apple")
                        .param("code", code)
                        .param("clientToken", clientToken)
                ).andExpect(status().isOk)
                    .andDocument(
                        "apple-login",
                        requestParameters(
                            "code" parameterTypeOf STRING parameterMeans "애플에서 제공해주는 인증 코드",
                            "clientToken" parameterTypeOf STRING parameterMeans "FCM Token"
                        )
                    ).andReturn()

                mvcResult.toResponse<TokenResponse>() shouldBe tokenResponse
            }
        }
    }

    Given("재로그인을 할 때") {
        val refreshTokenRequest = createRefreshTokenRequest()
        val idTokenResponse = createIdTokenResponse()
        every { appleLoginService.refreshAppleToken(refreshTokenRequest.refreshToken) } returns idTokenResponse
        When("POST $APPLE_LOGIN/refresh를 호출하면") {
            Then("정상적으로 APPLE JWT Token이 발행되어야한다.") {
                val mvcResult = mockMvc.perform(
                    post("$APPLE_LOGIN/refresh")
                        .content(refreshTokenRequest.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDocument(
                        "apple-relogin",
                        requestFields(
                            "refreshToken" typeOf STRING means "Apple의 RefreshToken"
                        ),
                        responseFields(
                            "idToken" typeOf STRING means "Apple의 JWT Token"
                        )
                    ).andReturn()

                mvcResult.toResponse<IdTokenResponse>() shouldBe idTokenResponse
            }
        }
    }

}) {
    companion object {
        const val APPLE_LOGIN = "/api/login"
    }
}

fun createAppleTokenResponse() = TokenResponse(
    idToken = "idToken",
    refreshToken = "refreshToken"
)

fun createRefreshTokenRequest() = RefreshTokenRequest(refreshToken = "refreshToken")
