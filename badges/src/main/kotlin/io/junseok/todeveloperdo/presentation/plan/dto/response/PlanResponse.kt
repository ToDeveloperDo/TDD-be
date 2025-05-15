package io.junseok.todeveloperdo.presentation.plan.dto.response

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import java.time.LocalDateTime

data class PlanResponse(
    val position: String,
    val stack: String,
    val experienceLevel: String,
    val targetPeriod: Int,
    val createDt: LocalDateTime,
    val planId: Long
){
    companion object{
        fun CurriculumPlan.toResponse() = PlanResponse(
            position = this.position,
            stack = this.stack,
            experienceLevel = this.experienceLevel,
            targetPeriod = this.targetPeriod,
            createDt = this.createDt!!,
            planId = this.planId!!
        )
    }
}
