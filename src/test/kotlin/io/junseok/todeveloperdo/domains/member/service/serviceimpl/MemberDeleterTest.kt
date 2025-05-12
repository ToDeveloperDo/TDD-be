package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class MemberDeleterTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>()
    val memberDeleter = MemberDeleter(memberRepository)
    Given("정상적인 사용자 닉네임을 받았을 때"){
        val username = "test"
        every { memberRepository.deleteByAppleId(username) } just runs
        When("removeMember()를 호출하면"){
            memberDeleter.removeMember(username)
            Then("정상적으로 삭제가 되어야한다."){
                verify(exactly = 1) { memberRepository.deleteByAppleId(username) }
            }
        }
    }

})
