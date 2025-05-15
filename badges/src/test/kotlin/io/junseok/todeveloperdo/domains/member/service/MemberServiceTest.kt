package io.junseok.todeveloperdo.domains.member.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.domains.memberfriend.service.createMemberResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.*

class MemberServiceTest : BehaviorSpec({
    val memberReader = mockk<MemberReader>()
    val memberUpdater = mockk<MemberUpdater>(relaxed = true)
    val memberValidator = mockk<MemberValidator>()
    val memberDeleter = mockk<MemberDeleter>(relaxed = true)
    val memberProcessor = mockk<MemberProcessor>()

    val memberService = MemberService(
        memberReader,
        memberUpdater,
        memberValidator,
        memberDeleter,
        memberProcessor
    )
    Given("Git계정을 등록할 때") {
        val gitUserResponse = GitUserResponse(
            username = "gitUser",
            avatarUrl = "avatarUrl",
            gitUrl = "gitUrl"
        )
        val accessToken = "testAccessToken"
        val username = "testUser"
        val member = createMember(1L, "appleId")
        every { memberReader.getMember(username) } returns member
        every { memberValidator.isExistGitMember(gitUserResponse.username) } returns false
        memberUpdater.updateGitMemberInfo(gitUserResponse, accessToken, member)
        When("Git계정을 처음 등록할 때") {
            memberService.createGitMember(gitUserResponse, accessToken, username)
            Then("Git회원 정보를 업데이트 해야한다.") {
                verify(atLeast = 1) {
                    memberUpdater.updateGitMemberInfo(
                        gitUserResponse,
                        accessToken,
                        member
                    )
                }
                verify(exactly = 0) {
                    memberUpdater.updateMemberToken(accessToken, member)
                }
            }
        }
        clearMocks(memberUpdater, memberValidator)

        When("이미 Git계정이 존재하는 경우") {
            every { memberValidator.isExistGitMember(gitUserResponse.username) } returns true
            memberUpdater.updateMemberToken(accessToken, member)
            memberService.createGitMember(gitUserResponse, accessToken, username)
            Then("Git Token을 업데이트 해야한다.") {
                verify(atLeast = 1) { memberUpdater.updateMemberToken(accessToken, member) }
                verify(exactly = 0) {
                    memberUpdater.updateGitMemberInfo(
                        gitUserResponse,
                        accessToken,
                        member
                    )
                }
            }
        }
    }


    Given("사용자 정보를 DTO로 변환하는 경우") {
        val userName = "username"
        val member = createMember(1L, "appleId")

        every { memberReader.getMember(userName) } returns member

        When("findMember()를 실행하면") {
            val result = memberService.findMember(userName)

            Then("반환 타입이 MemberInfoResponse 여야 한다.") {
                result.shouldBeTypeOf<MemberInfoResponse>()
            }
            Then("회원 정보가 올바르게 변환되어 반환된다") {
                result.username shouldBe member.gitHubUsername
                result.avatarUrl shouldBe member.avatarUrl
                result.gitUrl shouldBe member.gitHubUrl
            }
        }
    }

    Given("FCM토큰을 재발급 받는 경우") {
        val member = createMember(1L, "appleId")
        val fcmToken = "modify fcmToken"
        every { memberReader.getMember(member.appleId!!) } returns member
        When("유효한 appleId가 들어오면") {
            memberService.reIssued(member.appleId!!, fcmToken)
            Then("FCM토큰이 정상적으로 업데이트되어야한다.") {
                verify(exactly = 1) { memberUpdater.updateFcmToken(fcmToken, member) }
            }
        }
    }

    Given("회원 삭제 요청이 들어왔을 때") {
        val username = "appleId"

        every { memberDeleter.removeMember(username) } just runs

        When("deleteMember()를 호출하면") {
            memberService.deleteMember(username)

            Then("memberDeleter.removeMember가 호출되어야 한다") {
                verify(exactly = 1) { memberDeleter.removeMember(username) }
            }
        }
    }

    Given("전체 사용자를 조회할 때") {
        val appleId = "apple123"
        val expectedMembers = listOf(
            createMemberResponse(1L, appleId),
            createMemberResponse(2L, appleId)
        )

        every { memberProcessor.findMemberList(appleId) } returns expectedMembers

        When("findAllMember()를 호출하면") {
            val result = memberService.findAllMember(appleId)

            Then("memberProcessor.findMemberList가 호출되어야 한다") {
                verify(exactly = 1) { memberProcessor.findMemberList(appleId) }
            }

            Then("반환된 멤버 리스트는 기대한 값과 일치해야 한다") {
                result shouldBe expectedMembers
            }
        }
    }
})
