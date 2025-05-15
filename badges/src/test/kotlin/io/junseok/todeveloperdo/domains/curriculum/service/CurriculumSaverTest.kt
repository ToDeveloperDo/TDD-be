package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.domains.curriculum.content.service.ContentSaver
import io.junseok.todeveloperdo.domains.curriculum.content.service.createCurriculum
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import io.junseok.todeveloperdo.domains.curriculum.persistence.repository.CurriculumRepository
import io.junseok.todeveloperdo.domains.curriculum.persistence.toEntity
import io.junseok.todeveloperdo.domains.curriculum.plan.service.createCurriculumPlanTest
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*

class CurriculumSaverTest : BehaviorSpec({

    val curriculumRepository = mockk<CurriculumRepository>()
    val contentSaver = mockk<ContentSaver>(relaxed = true)
    val curriculumSaver = CurriculumSaver(curriculumRepository, contentSaver)

    Given("커리큘럼을 저장할 때") {
        val member = createMember(1L, "appleId")
        val plan = createCurriculumPlanTest(1L, member)

        val registerRequests = listOf(
            createRegisterRequest(1),
            createRegisterRequest(2)
        )
        every { curriculumRepository.save(any()) } answers { firstArg<Curriculum>() }

        When("save()를 호출하면") {
            curriculumSaver.save(registerRequests, member, plan)

            Then("커리큘럼이 2번 저장되어야 한다") {
                verify(exactly = 2) { curriculumRepository.save(any()) }
            }

            Then("각 요청마다 contentSaver가 호출되어야 한다") {
                verify(exactly = 2) { contentSaver.save(any(), any()) }
            }
        }
    }
})


fun createContentRequests(): List<ContentRequest> = listOf(
    ContentRequest(content = "강의 시청", isChecked = false),
    ContentRequest(content = "노션 정리", isChecked = true)
)

fun createRegisterRequest(week: Int = 1): RegisterRequest = RegisterRequest(
    weekTitle = "${week}주차",
    objective = "객체지향 개념 이해",
    contentRequests = createContentRequests()
)
