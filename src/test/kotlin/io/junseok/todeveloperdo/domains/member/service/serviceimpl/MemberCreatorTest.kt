package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberCreatorTest : BehaviorSpec({
    val memberCreator = MemberCreator()
    Given("멤버에 관한 정보가 입력되면"){
        val appleId = "testAppleId"
        val email = "test@example.com"
        val refreshToken = "refreshToken"
        val clientToken = "clientToken"
        When("generatorAppleMember를 호출하면"){
            val member = memberCreator.generatorAppleMember(
                appleId, email, refreshToken, clientToken
            )
            Then("입력값과 동일한 필드 값이 저장되어야 한다") {
                member.appleId shouldBe appleId
                member.appleEmail shouldBe email
                member.appleRefreshToken shouldBe refreshToken
                member.clientToken shouldBe clientToken
            }

            Then("멤버의 권한은 ROLE_USER이어야 한다."){
                member.authority shouldBe Authority.ROLE_USER
            }
        }
    }

})
