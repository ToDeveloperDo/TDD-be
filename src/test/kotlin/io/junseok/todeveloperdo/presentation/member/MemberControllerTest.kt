package io.junseok.todeveloperdo.presentation.member

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.service.createMemberResponse
import io.junseok.todeveloperdo.presentation.member.dto.request.FcmRequest
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.util.MockkPrincipal
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(MemberController::class)
@AutoConfigureRestDocs
class MemberControllerTest : BehaviorSpec({

    val memberService = mockk<MemberService>()

    val memberController = MemberController(memberService)
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(memberController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
        restDocumentation.beforeTest(javaClass, "MemberController")

    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("인증된 사용자가 멤버 정보를 조회할 때") {
        val userName = "testUser"
        val expectedResponse = MemberInfoResponse(
            username = "testUser",
            avatarUrl = "test",
            gitUrl = "gitUrl"
        )

        every { memberService.findMember(userName) } returns expectedResponse

        When("GET /api/member 요청을 보내면") {
            Then("멤버 정보를 정상적으로 반환한다") {
                val mvcResult = mockMvc.perform(
                    get("/api/member")
                        .principal(MockkPrincipal(userName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")

                )
                    .andExpect(status().isOk)
                    .andDo(
                        document(
                            "member-info",
                            authorizationHeader(),
                            responseFields(
                                "username" typeOf  STRING means "회원 이름",
                                "avatarUrl" typeOf STRING means "GitHub 프로필 URL",
                                "gitUrl" typeOf STRING means "GitHub URL",
                            )
                        )
                    )
                    .andReturn()
                mvcResult.toResponse<MemberInfoResponse>() shouldBe expectedResponse
                verify(exactly = 1) { memberService.findMember(userName) }
            }
        }
    }

    Given("회원 탈퇴를 하는 경우") {
        val userName = "testUser"
        every { memberService.deleteMember(userName) } just runs
        When("DELETE /api/member 요청을 보내면") {
            Then("정상적으로 탈퇴가 되어야한다.") {
                mockMvc.perform(
                    delete("/api/member")
                        .principal(MockkPrincipal(userName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer test-token")

                )
                    .andExpect(status().isOk)
                    .andDo(
                        document(
                            "delete-member",
                            authorizationHeader()
                        )
                    )
                verify(exactly = 1) { memberService.deleteMember(userName) }
            }
        }
    }

    Given("서비스에 등록된 사용자를 조회할 때") {
        val userName = "testUser"
        val memberResponses = listOf(
            createMemberResponse(1L, "apple"),
            createMemberResponse(2L, "apple"),
        )
        every { memberService.findAllMember(userName) } returns memberResponses
        When("GET /api/member/all를 호출하면") {
            Then("등록된 사용자 리스트가 반환되어야한다.") {
                val mvcResult = mockMvc.perform(
                    get("/api/member/all")
                        .header("Authorization", "Bearer test-token")
                        .principal(MockkPrincipal(userName))
                )
                    .andExpect(status().isOk)
                    .andDo(
                        document(
                            "find-all-user",
                            authorizationHeader(),
                            responseFields(
                                "memberId" arrayTypeOf NUMBER means "회원 ID",
                                "username" arrayTypeOf STRING means "회원 이름",
                                "avatarUrl" arrayTypeOf STRING means "GitHub 프로필 URL",
                                "gitUrl" arrayTypeOf STRING means "GitHub URL",
                                "friendStatus" arrayTypeOf STRING means "친구 상태"
                            )
                        )
                    )
                    .andReturn()

                mvcResult.toResponse<List<MemberResponse>>() shouldBe memberResponses
                verify(exactly = 1) { memberService.findAllMember(userName) }

            }
        }
    }

    Given("FCM토큰을 재발급 받을 때") {
        val userName = "testUser"
        val fcmRequest = FcmRequest("token")
        every { memberService.reIssued(any(), any()) } just runs
        When("POST /api/member/fcm 을 호출하면") {
            Then("FCM 토큰이 정상적으로 재발급되어야한다.") {
                mockMvc.perform(
                    post("/api/member/fcm")
                        .principal(MockkPrincipal(userName))
                        .content(fcmRequest.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                    .andExpect(status().isOk)
                    .andDo(
                        document(
                            "reissue-fcm-token",
                            requestFields(
                                "fcmToken" typeOf STRING means "FCM토큰"
                            ),
                        )
                    )

                verify(exactly = 1) { memberService.reIssued(userName, fcmRequest.fcmToken) }
            }
        }
    }
})
