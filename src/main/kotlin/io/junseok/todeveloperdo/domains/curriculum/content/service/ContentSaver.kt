package io.junseok.todeveloperdo.domains.curriculum.content.service

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.repository.ContentRepository
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.toEntity
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ContentSaver(
    private val contentRepository: ContentRepository,
) {
    @Transactional
    fun save(
        contentRequest: List<ContentRequest>,
        curriculum: Curriculum,
    ) {
        val contentList = contentRequest
            .map { content -> content.toEntity(curriculum) }
            .toList()
        contentRepository.saveAll(contentList)
    }
}