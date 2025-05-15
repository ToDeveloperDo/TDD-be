package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.presentation.plan.createPlanResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CurriculumPlanServiceTest : BehaviorSpec({
    val planReader = mockk<PlanReader>()
    val curriculumPlanService = CurriculumPlanService(planReader)

    Given("커리큘럼 계획 목록을 조회할 때") {
        val userName = "appleId"
        val expectedPlans =
            listOf(
                createPlanResponse(1),
                createPlanResponse(2)
            )

        every { planReader.findAll(userName) } returns expectedPlans

        When("findPlans를 호출하면") {
            val result = curriculumPlanService.findPlans(userName)

            Then("planReader의 findAll이 호출되어야 한다") {
                verify(exactly = 1) { planReader.findAll(userName) }
            }

            Then("반환된 결과는 PlanResponse 리스트여야 한다") {
                result shouldBe expectedPlans
            }
        }
    }

})
