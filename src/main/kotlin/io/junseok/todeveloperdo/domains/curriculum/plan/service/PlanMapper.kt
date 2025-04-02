package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

fun CurriculumRequest.toEntity(member: Member) = CurriculumPlan(
    position = this.position,
    stack = this.stack,
    experienceLevel = this.experienceLevel,
    targetPeriod = this.targetPeriod,
    member = member
)