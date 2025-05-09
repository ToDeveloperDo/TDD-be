package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequests
import io.junseok.todeveloperdo.domains.curriculum.plan.service.PlanGenerator
import io.junseok.todeveloperdo.domains.curriculum.plan.service.createCurriculumPlanTest
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.assertions.any
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlin.math.truncate

class CurriculumServiceTest : BehaviorSpec({
    val curriculumSaver = mockk<CurriculumSaver>()
    val memberReader = mockk<MemberReader>()
    val planGenerator = mockk<PlanGenerator>()
    val curriculumReader = mockk<CurriculumReader>()
    val service = CurriculumService(
        curriculumSaver,
        memberReader,
        planGenerator,
        curriculumReader
    )

    Given("생성된 커리큘럼을 저장할 때") {
        val registerRequests = RegisterRequests(
            listOf(
                RegisterRequest(
                    weekTitle = "weekTitle",
                    objective = "objective",
                    contentRequests = listOf(
                        ContentRequest(
                            content = "content",
                            isChecked = true
                        )
                    )
                )
            ),
            createCurriculumRequest()
        )
        val member = createMember(1, "appleId")
        val plan = createCurriculumPlanTest(1, member)
        every { memberReader.getMember(any()) } returns member
        every {
            planGenerator.create(
                registerRequests.curriculumRequest,
                any()
            )
        } returns plan
        every {
            curriculumSaver.save(
                eq(registerRequests.registerRequest),
                eq(member),
                eq(plan)
            )
        } just runs
        When("saveCurriculum()을 호춣하면") {
            service.saveCurriculum(registerRequests, member.gitHubUsername!!)
            Then("커리큘럼이 정상적으로 저장되어야한다.") {
                verify(exactly = 1) {
                    curriculumSaver.save(
                        registerRequests.registerRequest,
                        member,
                        plan
                    )
                }
            }
        }
    }
})
