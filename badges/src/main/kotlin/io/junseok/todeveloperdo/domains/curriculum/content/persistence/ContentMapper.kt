package io.junseok.todeveloperdo.domains.curriculum.content.persistence

import io.junseok.todeveloperdo.client.openai.dto.request.ContentRequest
import io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity.Content
import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum

fun ContentRequest.toEntity(curriculum: Curriculum) = Content(
    learnContent = this.content,
    isChecked = this.isChecked,
    curriculum = curriculum
)