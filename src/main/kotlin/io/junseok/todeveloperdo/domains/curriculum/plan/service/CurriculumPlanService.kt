package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.presentation.plan.dto.response.PlanResponse
import org.springframework.stereotype.Service

@Service
class CurriculumPlanService(
    private val planReader: PlanReader,
) {

    fun findPlans(userName: String): List<PlanResponse> = planReader.findAll(userName)
}