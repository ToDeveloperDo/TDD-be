package io.junseok.todeveloperdo.domains.curriculum.content.service

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity.Content
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.repository.ContentRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.mockk
import io.mockk.verify

class ContentSaverTest : BehaviorSpec({
    val contentRepository = mockk<ContentRepository>(relaxed = true)
    val contentSaver = ContentSaver(contentRepository)

    Given("ContentRequest 리스트와 Curriculum이 주어졌을 때") {
        val member = createMember(1, "appleId")

        val curriculum = createCurriculum(1, member)
        val contentRequests = listOf(
            ContentRequest("1주차 내용", false),
            ContentRequest("2주차 내용", false)
        )

        When("save()를 호출하면") {
            contentSaver.save(contentRequests, curriculum)
            Then("각 ContentRequest가 Entity로 변환되어 저장되어야 한다.") {
                verify {
                    contentRepository.saveAll(
                        match { list: List<Content> ->
                            list.size == 2 &&
                                    list[0].learnContent == "1주차 내용" &&
                                    list[0].curriculum == curriculum
                        }

                    )
                }
            }
        }
    }

})
