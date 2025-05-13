package io.junseok.todeveloperdo.oauth.apple.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.oauth.apple.client.AppleWithdrawClient
import io.junseok.todeveloperdo.oauth.apple.service.serviceimpl.ClientSecretCreator
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class AppleMemberServiceTest : FunSpec({
    val clientSecretCreator = mockk<ClientSecretCreator>()
    val memberSaver = mockk<MemberSaver>()
    val memberReader = mockk<MemberReader>()
    val memberValidator = mockk<MemberValidator>()
    val memberCreator = mockk<MemberCreator>()
    val memberDeleter = mockk<MemberDeleter>()
    val appleWithdrawClient = mockk<AppleWithdrawClient>()
    val clientId = "dummy-client-id"
    val appleMemberService = AppleMemberService(
        clientId = clientId,
        clientSecretCreator = clientSecretCreator,
        memberSaver = memberSaver,
        memberReader = memberReader,
        memberValidator = memberValidator,
        memberCreator = memberCreator,
        memberDeleter = memberDeleter,
        appleWithdrawClient = appleWithdrawClient
    )
    beforeTest {
        clearMocks(memberValidator, memberReader, memberCreator, memberSaver)
    }

    test("애플로그인으로 신규 사용자가 들어온 경우 정상적으로 계정이 생성되어야한다.") {
        val appleId = "appleId"
        val email = "appleEmail"
        val refreshToken = "appleRefreshToken"
        val clientToken = "Fcm"
        val member = createMember(1, appleId)
        every { memberValidator.isExistMember(appleId) } returns false
        every {
            memberCreator.generatorAppleMember(
                appleId,
                email,
                refreshToken,
                clientToken
            )
        } returns member
        every { memberSaver.saveMember(member) } returns member
        every { memberReader.getMember(appleId) } returns member
        appleMemberService.createOrUpdateMember(appleId, email, refreshToken, clientToken)

        verify(exactly = 1) {
            memberCreator.generatorAppleMember(
                appleId,
                email,
                refreshToken,
                clientToken
            )
        }
        verify(exactly = 1) { memberSaver.saveMember(member) }
        verify(exactly = 0) { memberReader.getMember(appleId) }
        member.appleEmail shouldBe email
        member.clientToken shouldBe clientToken
    }

    test("신규 사용자가 아닌 기존의 사용자가 애플로그인을 하면 clientToken이 업데이트 되어야한다.") {
        val appleId = "appleId"
        val email = "appleEmail"
        val refreshToken = "appleRefreshToken"
        val clientToken = "Fcm"
        val member = createMember(1, appleId)
        every { memberValidator.isExistMember(appleId) } returns true
        every { memberSaver.saveMember(member) } returns member
        every { memberReader.getMember(appleId) } returns member

        appleMemberService.createOrUpdateMember(appleId, email, refreshToken, clientToken)

        verify(exactly = 0) {
            memberCreator.generatorAppleMember(
                appleId,
                email,
                refreshToken,
                clientToken
            )
        }
        verify(exactly = 0) { memberSaver.saveMember(member) }
        verify(exactly = 1) { memberReader.getMember(appleId) }
    }

    test("유효한 애플 아이디를 revoke()에 넘기면 정상적으로 회원탈퇴가 된다.") {
        val appleId = "appleId"
        val member = createMember(1, appleId)
        val clientSecret = "clientSecret"

        every { memberReader.getMember(appleId) } returns member
        every { clientSecretCreator.createClientSecret() } returns clientSecret
        every {
            appleWithdrawClient.revokeToken(
                clientId,
                clientSecret,
                member.appleRefreshToken!!,
                "refresh_token"
            )
        } just runs
        every { memberDeleter.removeMember(appleId) } just runs

        appleMemberService.revoke(appleId)

        verify(exactly = 1) {
            appleWithdrawClient.revokeToken(
                clientId,
                clientSecret,
                member.appleRefreshToken!!,
                "refresh_token"
            )
        }
        verify(exactly = 1) { memberDeleter.removeMember(appleId) }
    }

    test("비정상적인 값이 넘어오면 예외가 발생해야한다."){
        val appleId = "appleId"
        val member = createMember(1, appleId)
        val clientSecret = "clientSecret"

        every { memberReader.getMember(appleId) } returns member
        every { clientSecretCreator.createClientSecret() } returns clientSecret
        every {
            appleWithdrawClient.revokeToken(
                clientId,
                clientSecret,
                member.appleRefreshToken!!,
                "refresh_token"
            )
        } throws Exception("유효하지 않는 토큰")

        throwsWith<Exception>(
            {
                appleMemberService.revoke(appleId)
            },
            {
                ex -> ex.message shouldBe "유효하지 않는 토큰"
            }
        )
    }
})
