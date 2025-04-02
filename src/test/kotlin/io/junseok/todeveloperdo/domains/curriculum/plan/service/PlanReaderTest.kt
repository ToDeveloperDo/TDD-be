package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.repository.CurriculumPlanRepository
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.throwsWith
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.hibernate.annotations.WhereJoinTable
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

class PlanReaderTest : BehaviorSpec({
    val curriculumPlanRepository = mockk<CurriculumPlanRepository>()
    val memberReader = mockk<MemberReader>()
    val planReader = PlanReader(curriculumPlanRepository, memberReader)

    Given("사용자의 Plan을 조회하는 경우") {
        val member = createMember(1, "appleId")
        val curriculumPlan =
            (1L..3L).map { id ->
                createCurriculumPlanTest(id, member)
            }
        every { memberReader.getMember("appleId") } returns member
        every { curriculumPlanRepository.findAllByMember(member) } returns curriculumPlan
        When("findAll()를 호출하면") {
            val result = planReader.findAll(member.appleId!!)
            Then("Plan목록이 정상적으로 반환되어야한다.") {
                result shouldHaveSize curriculumPlan.size
            }
            Then("각 Plan이 매핑된 Response와 일치해야 한다") {
                result.zip(curriculumPlan).all { (response, entity) ->
                    response.planId == entity.planId &&
                            response.stack == entity.stack &&
                            response.position == entity.position &&
                            response.experienceLevel == entity.experienceLevel
                } shouldBe true
            }
        }
    }

    Given("planId로 커리큘럼 플랜을 조회할 때") {
        val member = createMember(1, "appleId")
        val curriculumPlan = createCurriculumPlanTest(1, member)

        And("planId에 맞는 플랜이 있는 경우") {
            every {
                curriculumPlanRepository.findByIdOrNull(curriculumPlan.planId)
            } returns curriculumPlan
            val result = planReader.findById(curriculumPlan.planId!!)
            Then("정상적으로 값이 반환되어야한다.") {
                result.planId shouldBe curriculumPlan.planId
            }
        }

        And("planId에 맞는 플랜이 없는 경우") {
            every {
                curriculumPlanRepository.findByIdOrNull(2)
            } returns null
            Then("NOT_EXIST_PLAN예외가 발생해야한다.") {
                throwsWith<ToDeveloperDoException>(
                    {
                        planReader.findById(2)
                    },
                    { ex -> ex.errorCode shouldBe ErrorCode.NOT_EXIST_PLAN }
                )
            }
        }
    }
})

fun createCurriculumPlanTest(planId: Long, member: Member): CurriculumPlan {
    return CurriculumPlan(
        planId = planId,
        position = "position",
        stack = "stack",
        experienceLevel = "experienceLevel",
        targetPeriod = 3,
        member = member
    ).apply {
        this.createDt = LocalDateTime.of(2025, 4, 2, 14, 0)
    }
}