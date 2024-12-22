package io.junseok.todeveloperdo.domains.curriculum.plan.service

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.repository.CurriculumPlanRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.plan.dto.response.PlanResponse
import io.junseok.todeveloperdo.presentation.plan.dto.response.PlanResponse.Companion.toResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PlanReader(
    private val curriculumPlanRepository: CurriculumPlanRepository,
    private val memberReader: MemberReader,
) {
    @Transactional(readOnly = true)
    fun findAll(userName: String): List<PlanResponse> {
        val member = memberReader.getMember(userName)
        return curriculumPlanRepository.findAllByMember(member)
            .map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun findById(planId: Long) =
        curriculumPlanRepository.findByIdOrNull(planId)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_PLAN }
}