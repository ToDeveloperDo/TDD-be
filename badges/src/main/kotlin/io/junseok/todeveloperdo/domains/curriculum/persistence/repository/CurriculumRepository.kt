package io.junseok.todeveloperdo.domains.curriculum.persistence.repository

import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import org.springframework.data.jpa.repository.JpaRepository

interface CurriculumRepository : JpaRepository<Curriculum, Long> {
    fun findAllByCurriculumPlan(curriculumPlan: CurriculumPlan): List<Curriculum>
}