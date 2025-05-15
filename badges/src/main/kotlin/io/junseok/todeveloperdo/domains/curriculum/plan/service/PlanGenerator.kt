package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.client.openai.dto.request.CurriculumRequest
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.repository.CurriculumPlanRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PlanGenerator(
    private val curriculumPlanRepository: CurriculumPlanRepository,
    private val memberReader: MemberReader
) {
    @Transactional
    fun create(curriculumRequest: CurriculumRequest, userName: String): CurriculumPlan{
        val member = memberReader.getMember(userName)
        val curriculumPlan = curriculumRequest.toEntity(member)
        return curriculumPlanRepository.save(curriculumPlan)
    }
}