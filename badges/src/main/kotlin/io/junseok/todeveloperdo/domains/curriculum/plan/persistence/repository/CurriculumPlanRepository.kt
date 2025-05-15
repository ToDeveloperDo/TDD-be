package io.junseok.todeveloperdo.domains.curriculum.plan.persistence.repository

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface CurriculumPlanRepository : JpaRepository<CurriculumPlan, Long> {
    fun findAllByMember(member: Member): List<CurriculumPlan>
}