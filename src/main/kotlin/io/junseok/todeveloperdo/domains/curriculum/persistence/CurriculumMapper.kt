package io.junseok.todeveloperdo.domains.curriculum.persistence

import io.junseok.todeveloperdo.client.openai.dto.request.RegisterRequest
import io.junseok.todeveloperdo.client.openai.dto.response.ContentResponse
import io.junseok.todeveloperdo.client.openai.dto.response.CurriculumResponse
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity.Content
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

fun RegisterRequest.toEntity(member: Member, plan: CurriculumPlan) = Curriculum(
    weekTitle = this.weekTitle,
    objective = this.objective,
    member = member,
    curriculumPlan = plan
)

fun Curriculum.fromResponse(content: List<Content>) = CurriculumResponse(
    weekTitle = this.weekTitle,
    objective = this.objective,
    contentRequests = content.map { it.fromResponse() }
)

fun Content.fromResponse() = ContentResponse(
    content = this.learnContent,
    isChecked = this.isChecked
)