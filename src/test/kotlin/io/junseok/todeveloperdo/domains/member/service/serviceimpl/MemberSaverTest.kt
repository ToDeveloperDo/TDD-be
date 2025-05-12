package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*

class MemberSaverTest : BehaviorSpec({
    val memberRepository = mockk<MemberRepository>()
    val memberSaver = MemberSaver(memberRepository)
    Given("Member Entity가 정상적으로 넘어온 경우"){
        val member = createMember(1, "appleId")
        every { memberRepository.save(member) } returns member
        When("새로운 유저가 회원가입을 진행할 때"){
            memberSaver.saveMember(member)
            Then("정상적으로 DB에 저장이 되어야한다."){
                verify(exactly = 1) { memberRepository.save(member) }

            }
        }
    }
})
