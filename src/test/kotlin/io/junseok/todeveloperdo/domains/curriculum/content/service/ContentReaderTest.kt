package io.junseok.todeveloperdo.domains.curriculum.content.service

import io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity.Content
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.repository.ContentRepository
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import io.junseok.todeveloperdo.domains.curriculum.plan.service.createCurriculumPlanTest
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ContentReaderTest : BehaviorSpec({
    val contentRepository = mockk<ContentRepository>()
    val contentReader = ContentReader(contentRepository)

    Given("생성된 커리큘럼의 컨텐츠를 불러올 때") {
        val member = createMember(1, "appleId")
        val curriculum = createCurriculum(1, member)
        val expectedContents = (1L..3L).map { id ->
            createContent(id, curriculum)
        }
        every { contentRepository.findAllByCurriculum(curriculum) } returns expectedContents
        When("find()를 호출하면") {
            val result = contentReader.find(curriculum)
            Then("컨텐츠 목록이 정상적으로 반환되어야한다.") {
                result shouldHaveSize expectedContents.size
            }
            Then("각 컨텐츠의 ID와 학습 내용이 일치해야 한다") {
                result.map { it.contentId } shouldBe expectedContents.map { it.contentId }
                result.map { it.learnContent } shouldBe expectedContents.map { it.learnContent }
            }
            Then("컨텐츠가 없는 경우 빈 리스트가 반환되어야 한다") {
                every { contentRepository.findAllByCurriculum(any()) } returns emptyList()
                val results = contentReader.find(curriculum)
                results shouldBe emptyList()
            }
        }
    }
})

fun createCurriculum(curriculumId: Long, member:Member) = Curriculum(
    curriculumId = curriculumId,
    weekTitle = "weekTitle",
    objective = "objective",
    member = member,
    curriculumPlan = createCurriculumPlanTest(1, member)
)

fun createContent(contentId: Long, curriculum: Curriculum) = Content(
    contentId = contentId,
    learnContent = "learnContent",
    isChecked = false,
    curriculum = curriculum
)