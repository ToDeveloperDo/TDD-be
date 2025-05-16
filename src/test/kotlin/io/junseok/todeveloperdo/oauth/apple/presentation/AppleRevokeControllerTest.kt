package io.junseok.todeveloperdo.oauth.apple.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.oauth.apple.service.AppleMemberService
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.andDocument
import io.junseok.todeveloperdo.util.dsl.authorizationHeader
import io.junseok.todeveloperdo.util.setAuthorization
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(AppleRevokeController::class)
@AutoConfigureRestDocs
class AppleRevokeControllerTest : BehaviorSpec({
    val appleMemberService = mockk<AppleMemberService>()
    val appleRevokeController = AppleRevokeController(appleMemberService)

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(appleRevokeController)
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
        restDocumentation.beforeTest(javaClass, "AppleRevokeController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("사용자가 회원탈퇴를 할 때") {
        every { appleMemberService.revoke(any()) } just runs
        When("POST /api/apple을 호출하면") {
            Then("정상적으로 탈퇴가 되어야한다.") {
                mockMvc.perform(
                    post(APPLE_REVOKE)
                        .setAuthorization()
                ).andExpect(status().isOk)
                    .andDocument(
                        "revoke-apple",
                        authorizationHeader()
                    )

                verify(exactly = 1) { appleMemberService.revoke("username") }
            }
        }
    }
}) {
    companion object {
        const val APPLE_REVOKE = "/api/apple/"
    }
}