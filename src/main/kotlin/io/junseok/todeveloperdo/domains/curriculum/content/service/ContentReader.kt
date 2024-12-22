package io.junseok.todeveloperdo.domains.curriculum.content.service

import io.junseok.todeveloperdo.domains.curriculum.content.persistence.repository.ContentRepository
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ContentReader(private val contentRepository: ContentRepository) {
    @Transactional(readOnly = true)
    fun find(curriculum: Curriculum) = contentRepository.findAllByCurriculum(curriculum)
}