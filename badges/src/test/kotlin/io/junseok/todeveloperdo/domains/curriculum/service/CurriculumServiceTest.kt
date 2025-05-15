package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequests
import io.junseok.todeveloperdo.client.openai.dto.response.ContentResponse
import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.plan.service.PlanGenerator
import io.junseok.todeveloperdo.domains.curriculum.plan.service.createCurriculumPlanTest
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.assertions.fail
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

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

    Given("커리큘럼 목록을 조회할 때") {
        And("커리큘럼이 존재한다면") {
            val curriculumResponse = listOf(createCurriculumResponse())
            every { curriculumReader.findByPlan(1L) } returns curriculumResponse
            When("조회하면") {
                val result = service.find(1L)
                Then("List<CurriculumResponse>가 반환되어야 한다") {
                    result shouldBe curriculumResponse
                }
            }
        }

        And("커리큘럼이 존재하지 않는다면") {
            every { curriculumReader.findByPlan(1L) } returns emptyList()
            When("조회하면") {
                val result = service.find(1L)
                Then("빈 배열이 반환되어야 한다") {
                    result shouldBe emptyList()
                }
            }
        }
    }

})

fun createCurriculumResponse() = CurriculumResponse(
    weekTitle = "weekTitle",
    objective = "objective",
    contentRequests = listOf(createContentResponse())
)

fun createContentResponse() = ContentResponse(
    content = "content",
    isChecked = false
)
