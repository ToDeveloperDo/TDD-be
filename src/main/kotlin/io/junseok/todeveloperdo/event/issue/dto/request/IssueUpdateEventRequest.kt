package io.junseok.todeveloperdo.event.issue.dto.request

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

data class IssueUpdateEventRequest(
    val member: Member,
    val issueNumber: Int,
    val todoCreate: TodoCreate
)
