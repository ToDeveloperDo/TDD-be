package io.junseok.todeveloperdo.oauth.git.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.oauth.git.service.GitHubOAuthService
import io.junseok.todeveloperdo.oauth.git.service.createTokenResponse
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.STRING
import io.junseok.todeveloperdo.util.dsl.parameterTypeOf
import io.junseok.todeveloperdo.util.dsl.requestParameters
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.startsWith
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(LoginController::class)
@AutoConfigureRestDocs
class LoginControllerTest : BehaviorSpec({
    val gitHubOAuthService = mockk<GitHubOAuthService>()
    val clientId = "clientId"
    val redirectUri = "redirectUri"
    val loginController = LoginController(
        gitHubOAuthService,
        clientId,
        redirectUri
    )
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(loginController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
        restDocumentation.beforeTest(javaClass, "GitHubController")

    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("GitHub 로그인 시도할 때") {
        val appleId = "appleId"
        When("GET /git/login 요청하면") {
            Then("GitHub 인증 페이지로 리디렉션된다") {
                mockMvc.perform(
                    get("/git/login")
                        .param("appleId", appleId)
                ).andExpect(status().is3xxRedirection)
                    .andExpect(
                        header().string(
                            "Location",
                            startsWith("https://github.com/login/oauth/authorize")
                        )
                    )

                    .andDo(
                        document(
                            "redirect-to-github",
                            requestParameters(
                                "appleId" parameterTypeOf STRING parameterMeans "애플 ID"
                            )
                        )
                    )
            }
        }
    }

    Given("GitHub에서 로그인 콜백을 수신하면") {
        val tokenResponse = createTokenResponse()
        val code = "code"
        val appleId = "appleId"
        every { gitHubOAuthService.processGitHubOAuth(code,appleId) } returns tokenResponse

        When("GET /login/oauth2/code/github 요청") {
            Then("딥링크로 리디렉션되고 토큰이 포함된다") {
                mockMvc.perform(
                    get("/login/oauth2/code/github")
                        .param("code",code)
                        .param("state",appleId)
                )   .andExpect(status().is3xxRedirection)
                    .andExpect(header().string("Location", "myapp://callback?token=${tokenResponse.token}"))
                    .andDo(
                        document(
                            "callback-from-github",
                            requestParameters(
                                "code" parameterTypeOf STRING parameterMeans "GitHub에서 전달된 인증 코드",
                                "state" parameterTypeOf STRING parameterMeans "초기 로그인 시 전달한 Apple ID"
                            )
                        )
                    )
                verify(exactly = 1) { gitHubOAuthService.processGitHubOAuth(code,appleId) }
            }
        }
    }
})
