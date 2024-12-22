package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.domains.curriculum.content.service.ContentSaver
import io.junseok.todeveloperdo.domains.curriculum.persistence.repository.CurriculumRepository
import io.junseok.todeveloperdo.domains.curriculum.persistence.toEntity
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CurriculumSaver(
    private val curriculumRepository: CurriculumRepository,
    private val contentSaver: ContentSaver
) {
    @Transactional
    fun save(
        registers: List<RegisterRequest>,
        member: Member,
        plan: CurriculumPlan
    ) {
        registers.forEach {
            val curriculum = it.toEntity(member,plan)
            curriculumRepository.save(curriculum)
            contentSaver.save(it.contentRequests,curriculum)
        }
    }
}