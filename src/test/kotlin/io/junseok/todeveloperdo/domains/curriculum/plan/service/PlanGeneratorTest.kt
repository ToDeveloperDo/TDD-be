package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.repository.CurriculumPlanRepository
import io.junseok.todeveloperdo.domains.curriculum.service.createCurriculumRequest
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class PlanGeneratorTest : BehaviorSpec({
    val curriculumPlanRepository = mockk<CurriculumPlanRepository>()
    val memberReader = mockk<MemberReader>()
    val planGenerator = PlanGenerator(curriculumPlanRepository, memberReader)

    Given("새로운 커리큘럼을 생성할 때") {
        val member = createMember(1, "appleId")
        val curriculumPlan = createCurriculumPlan(1L, member)
        val curriculumRequest = createCurriculumRequest()

        every { memberReader.getMember(member.gitHubUsername!!) } returns member
        every { curriculumPlanRepository.save(any()) } returns curriculumPlan

        When("create()를 호출하면") {
            val result: CurriculumPlan = planGenerator.create(curriculumRequest, member.gitHubUsername!!)
            Then("커리큘럼이 정상적으로 생성되어야 한다.") {
                result shouldBe curriculumPlan
                verify(exactly = 1) { memberReader.getMember(member.gitHubUsername!!) }
                verify(exactly = 1) { curriculumPlanRepository.save(any()) }
            }

        }
    }
})

fun createCurriculumPlan(planId: Long, member: Member) = CurriculumPlan(
    planId = planId,
    position = "position",
    stack = "stack",
    experienceLevel = "experienceLevel",
    targetPeriod = 1,
    member = member
)