package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MemberValidatorTest : BehaviorSpec({
    val memberReader = mockk<MemberReader>()
    val memberRepository = mockk<MemberRepository>()
    val memberValidator = MemberValidator(memberReader, memberRepository)

    Given("유저의 깃허브 레포 존재 유무를 확인할 때") {
        val appleId = "test"
        When("레포가 존재한다면") {
            every { memberReader.getMember(appleId) } returns createMember(1L, "appleId", "gitRepo")
            val result = memberValidator.isExistRepo(appleId)
            Then("레포명을 반환해야 한다.") {
                result shouldBe "gitRepo"
            }
        }
        When("레포가 존재하지 않는다면") {
            every { memberReader.getMember(appleId) } returns createMember(1L, "appleId")
            Then("ToDeveloperDoException이 발생해야 한다") {
                shouldThrow<ToDeveloperDoException> {
                    memberValidator.isExistRepo(appleId)
                }.errorCode shouldBe ErrorCode.NOT_EXIST_REPO
            }
        }
    }

    Given("사용자 닉네임으로 조회할 때") {
        val member = createMember(1L, "test")
        every { memberRepository.existsByGitHubUsername(member.gitHubUsername!!) } returns true
        When("닉네임이 존재하는경우") {
            val result = memberValidator.isExistGitMember(member.gitHubUsername!!)
            Then("정상적으로 사용자가 조회되어야한다.") {
                result shouldBe true
            }
        }

        When("닉네임이 존재하지 않는 경우") {
            every { memberRepository.existsByGitHubUsername(member.gitHubUsername!!) } returns false
            val result = memberValidator.isExistGitMember(member.gitHubUsername!!)
            Then("false가 반환되어야한다.") {
                result shouldBe false
            }
        }
    }

    Given("사용자 AppleId로 조회할 때") {
        val member = createMember(1L, "test")
        every { memberRepository.existsByAppleId(member.appleId!!) } returns true
        When("AppleId가 존재하는경우") {
            val result = memberValidator.isExistMember(member.appleId!!)
            Then("정상적으로 사용자가 조회되어야한다.") {
                result shouldBe true
            }
        }

        When("AppleId가 존재하지 않는 경우") {
            every { memberRepository.existsByAppleId(member.appleId!!) } returns false
            val result = memberValidator.isExistMember(member.appleId!!)
            Then("false가 반환되어야한다.") {
                result shouldBe false
            }
        }
    }
})
