package io.junseok.todeveloperdo.domains.curriculum.content.persistence.repository

import io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity.Content
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import org.springframework.data.jpa.repository.JpaRepository

interface ContentRepository : JpaRepository<Content, Long> {
    fun findAllByCurriculum(curriculum: Curriculum): List<Content>
}