package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.domains.curriculum.content.service.ContentReader
import io.junseok.todeveloperdo.domains.curriculum.content.service.createContent
import io.junseok.todeveloperdo.domains.curriculum.content.service.createCurriculum
import io.junseok.todeveloperdo.domains.curriculum.persistence.repository.CurriculumRepository
import io.junseok.todeveloperdo.domains.curriculum.plan.service.PlanReader
import io.junseok.todeveloperdo.domains.curriculum.plan.service.createCurriculumPlanTest
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class CurriculumReaderTest : BehaviorSpec({
    val curriculumRepository = mockk<CurriculumRepository>()
    val planReader = mockk<PlanReader>()
    val contentReader = mockk<ContentReader>()

    val curriculumReader = CurriculumReader(curriculumRepository, planReader, contentReader)

    Given("플랜을 조회할 때") {
        val member = createMember(1, "appleId")
        val curriculumPlan = createCurriculumPlanTest(1, member)
        val curriculums = (1L..3L).map { id ->
            createCurriculum(id, member)
        }
        val contents = curriculums.map { curriculum ->
            listOf(
                createContent(1L, curriculum),
                createContent(2L, curriculum)
            )
        }
        every { planReader.findById(1) } returns curriculumPlan
        every {
            curriculumRepository.findAllByCurriculumPlan(curriculumPlan)
        } returns curriculums

        curriculums.forEachIndexed { index, curriculum ->
            every { contentReader.find(curriculum) } returns contents[index]
        }

        When("findByPlan()을 호출하면") {
            val result = curriculumReader.findByPlan(1)
            Then("플랜이 정상적으로 반환되어야한다.") {
                result shouldHaveSize curriculums.size
            }
            Then("각 응답은 콘텐츠 정보를 포함해야 한다") {
                result.map { it.weekTitle } shouldBe curriculums.map { it.weekTitle }
            }
        }
    }
})
