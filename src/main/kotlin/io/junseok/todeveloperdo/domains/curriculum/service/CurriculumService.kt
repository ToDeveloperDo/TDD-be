package io.junseok.todeveloperdo.domains.curriculum.service

import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequests
import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.plan.service.PlanGenerator
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import org.springframework.stereotype.Service

@Service
class CurriculumService(
    private val curriculumSaver: CurriculumSaver,
    private val memberReader: MemberReader,
    private val planGenerator: PlanGenerator,
    private val curriculumReader: CurriculumReader
) {
    fun saveCurriculum(
        registerRequests: RegisterRequests,
        userName: String
    ){
        val member = memberReader.getMember(userName)
        val plan = planGenerator.create(registerRequests.curriculumRequest, userName)

        curriculumSaver.save(registerRequests.registerRequest,member,plan)
    }

    fun find(planId: Long): List<CurriculumResponse> = curriculumReader.findByPlan(planId)
}