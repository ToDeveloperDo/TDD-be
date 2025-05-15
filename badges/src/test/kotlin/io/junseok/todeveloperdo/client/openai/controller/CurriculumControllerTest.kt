package io.junseok.todeveloperdo.client.openai.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequests
import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.service.*
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.setAuthorization
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@WebMvcTest(CurriculumController::class)
@AutoConfigureRestDocs
class CurriculumControllerTest : BehaviorSpec({
    val curriculumProcessor = mockk<CurriculumProcessor>()
    val curriculumService = mockk<CurriculumService>()
    val curriculumController = CurriculumController(
        curriculumProcessor,
        curriculumService
    )
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(curriculumController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
            .registerModules(JavaTimeModule())
        restDocumentation.beforeTest(javaClass, "CurriculumController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    Given("커리큘럼을 생성할 때") {
        val curriculumRequest = createCurriculumRequest()
        val result = "result"
        every { curriculumProcessor.recommendCurriculum(curriculumRequest) } returns result
        When("POST $CURRICULUM_PATH/recommend를 호출하면") {
            Then("정상적으로 커리큘럼이 생성되어야한다.") {
                mockMvc.perform(
                    post("$CURRICULUM_PATH/recommend")
                        .setAuthorization()
                        .content(curriculumRequest.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "create-curriculum",
                            requestFields(
                                "position" typeOf STRING means "직군",
                                "stack" typeOf STRING means "기술 스택",
                                "experienceLevel" typeOf STRING means "경험 수준(상,중,하)",
                                "targetPeriod" typeOf NUMBER means "커리큘럼 수행 기간(달)"
                            )
                        )
                    )
                verify(exactly = 1) { curriculumProcessor.recommendCurriculum(curriculumRequest) }
            }
        }
    }

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
        every { curriculumService.saveCurriculum(registerRequests, any()) } just runs
        When("POST $CURRICULUM_PATH/save를 호출하면") {
            Then("생성된 커리큘럼이 정상적으로 저장되어야한다.") {
                mockMvc.perform(
                    post("$CURRICULUM_PATH/save")
                        .setAuthorization()
                        .content(registerRequests.toRequest())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "save-curriculum",
                            requestFields(
                                "curriculumRequest.position" typeOf STRING means "직군",
                                "curriculumRequest.stack" typeOf STRING means "기술 스택",
                                "curriculumRequest.experienceLevel" typeOf STRING means "경험 수준 (상/중/하)",
                                "curriculumRequest.targetPeriod" typeOf NUMBER means "커리큘럼 수행 기간 (달)",

                                "registerRequest[].weekTitle" typeOf STRING means "주차 제목",
                                "registerRequest[].objective" typeOf STRING means "학습 목표",
                                "registerRequest[].contentRequests[].content" typeOf STRING means "세부 콘텐츠 설명",
                                "registerRequest[].contentRequests[].isChecked" typeOf BOOLEAN means "완료 여부"
                            )
                        )
                    )
            }
        }
    }

    Given("커리큘럼을 조회할 때") {
        val curriculumResponses = listOf(createCurriculumResponse())
        every { curriculumService.find(any()) } returns curriculumResponses
        When("POST $CURRICULUM_PATH/{planId}를 호출하면") {
            Then("정상적으로 커리큘럼 목록이 조회되어야한다.") {
                val mvcResult = mockMvc.perform(
                    get("$CURRICULUM_PATH/{planId}", 1L)
                        .setAuthorization()
                ).andExpect(status().isOk)
                    .andDo(
                        document(
                            "find-curriculum-list",
                            responseFields(
                                "[].weekTitle" typeOf STRING means "주차 제목",
                                "[].objective" typeOf STRING means "학습 목표",
                                "contentRequests[].content" arrayTypeOf STRING means "학습 콘텐츠 내용",
                                "contentRequests[].isChecked" arrayTypeOf BOOLEAN means "해당 콘텐츠 완료 여부"
                            )
                        )
                    ).andReturn()

                mvcResult.toResponse<List<CurriculumResponse>>() shouldBe curriculumResponses

            }
        }
    }
}) {
    companion object {
        const val CURRICULUM_PATH = "/api/curriculum"
    }
}
