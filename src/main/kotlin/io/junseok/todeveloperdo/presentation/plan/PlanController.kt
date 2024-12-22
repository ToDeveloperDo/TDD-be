package io.junseok.todeveloperdo.presentation.plan

import io.junseok.todeveloperdo.domains.curriculum.plan.service.CurriculumPlanService
import io.junseok.todeveloperdo.presentation.plan.dto.response.PlanResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/plan")
@CrossOrigin
class PlanController(
    private val planService: CurriculumPlanService
) {

    // TODO -> 생성한 커리큘럼 목록 조회
    @GetMapping
    fun getAllPlan(principal: Principal): ResponseEntity<List<PlanResponse>> =
        ResponseEntity.ok(planService.findPlans(principal.name))
}