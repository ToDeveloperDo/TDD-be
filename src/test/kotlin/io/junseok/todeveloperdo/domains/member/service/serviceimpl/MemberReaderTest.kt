package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MemberReaderTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>()
    val memberReader = MemberReader(memberRepository)
    Given("닉네임으로 사용자를 조회할 때") {
        val nickname = "TestName"
        val member = createMember(1L, "TestName")

        When("정상적으로 닉네임이 들어왔을 때") {
            every { memberRepository.findByAppleId(nickname) } returns member

            val result = memberReader.getMember(nickname)
            Then("정상 응답") {
                result.memberId shouldBe 1L
                result.appleId shouldBe "TestName"
            }
        }

        When("존재하지 않는 닉네임인 경우") {
            every { memberReader.getMember("not") } throws ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }
            Then("존재하지 않는 에러 응답") {
                val exception = shouldThrow<ToDeveloperDoException> {
                    memberReader.getMember("not")
                }
                exception.errorCode shouldBe ErrorCode.NOT_EXIST_MEMBER
            }
        }
    }

    Given("나를 제외한 멤버 목록을 조회할 때") {
        val me = createMember(1L, "test")
        val memberList = listOf(
            createMember(2L, "TestOther"),
            createMember(3L, "TestOther2")
        )

        every { memberRepository.findAll() } returns listOf(me)+memberList
        When("나를 제외한 멤버를 조회화면"){
            val result = memberReader.getMembersExcludeMe(me)
            Then("나를 제외한 다른 멤버들만 조회되어야 한다"){
                result shouldBe memberList
            }
        }
    }
})

fun createMember(id: Long, appleId: String,repo: String?=null) = Member(
    memberId = id,
    appleId = appleId,
    appleRefreshToken = "appleRefreshToken",
    appleEmail = "appleEmail",
    gitHubUsername = "username",
    gitHubToken = "gitToken",
    gitHubRepo = repo,
    avatarUrl = "avatar",
    gitHubUrl = "gitUrl",
    clientToken = "Fcm",
    authority = Authority.ROLE_USER
)

