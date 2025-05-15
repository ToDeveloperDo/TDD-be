package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.content.service.ContentReader
import io.junseok.todeveloperdo.domains.curriculum.persistence.fromResponse
import io.junseok.todeveloperdo.domains.curriculum.persistence.repository.CurriculumRepository
import io.junseok.todeveloperdo.domains.curriculum.plan.service.PlanReader
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CurriculumReader(
    private val curriculumRepository: CurriculumRepository,
    private val planReader: PlanReader,
    private val contentReader: ContentReader
) {

    @Transactional(readOnly = true)
    fun findByPlan(planId:Long): List<CurriculumResponse>{
        val planEntity = planReader.findById(planId)
        return curriculumRepository.findAllByCurriculumPlan(planEntity)
            .map { it.fromResponse(contentReader.find(it)) }
    }
}