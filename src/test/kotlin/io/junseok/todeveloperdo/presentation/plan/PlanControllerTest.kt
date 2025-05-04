package io.junseok.todeveloperdo.presentation.plan

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.domains.curriculum.plan.service.CurriculumPlanService
import io.junseok.todeveloperdo.presentation.plan.dto.response.PlanResponse
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.setAuthorization
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDateTime

@WebMvcTest(PlanController::class)
@AutoConfigureRestDocs
class PlanControllerTest : FunSpec({
    val planService = mockk<CurriculumPlanService>()
    val planController = PlanController(planService)

    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(planController)
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
        restDocumentation.beforeTest(javaClass, "MemberController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    test("생성한 커리큘럼 목록 조회 API") {
        val planResponses = listOf(
            createPlanResponse(1),
            createPlanResponse(2)
        )
        every { planService.findPlans(any()) } returns planResponses

        val mvcResult = mockMvc.perform(
            get("/api/plan")
                .setAuthorization()
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "show-all-plan",
                    authorizationHeader(),
                    responseFields(
                        "planId" arrayTypeOf NUMBER means "생성된 플랜 ID",
                        "position" arrayTypeOf STRING means "개발 포지션",
                        "stack" arrayTypeOf STRING means "기술 스택",
                        "experienceLevel" arrayTypeOf STRING means "경험 수준",
                        "targetPeriod" arrayTypeOf NUMBER means "희망 학습 기간",
                        "createDt" arrayTypeOf ARRAY means "생성된 일자"
                    )
                )
            ).andReturn()

        mvcResult.toResponse<List<PlanResponse>>() shouldBe planResponses
    }
})

fun createPlanResponse(planId: Long) = PlanResponse(
    planId = planId,
    position = "position",
    stack = "stack",
    experienceLevel = "experienceLevel",
    targetPeriod = 1,
    createDt = LocalDateTime.now()
)
