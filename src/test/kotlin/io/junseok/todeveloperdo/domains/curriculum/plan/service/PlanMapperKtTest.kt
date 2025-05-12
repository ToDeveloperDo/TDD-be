package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.domains.curriculum.service.createCurriculumRequest
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PlanMapperKtTest : BehaviorSpec({
    Given("CurriculumRequest가 주어졌을 때") {
        val member = createMember(1L, "appleId")
        val request = createCurriculumRequest()
        When("toEntity()를 호출하면") {
            val entity = request.toEntity(member)

            Then("동일한 필드 값을 가진 CurriculumPlan이 생성되어야 한다") {
                entity.position shouldBe "position"
                entity.stack shouldBe "stack"
                entity.experienceLevel shouldBe "experienceLevel"
                entity.targetPeriod shouldBe 1
                entity.member shouldBe member
            }
        }
    }
})
