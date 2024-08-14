package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component

@Component
class GitIssueCreator {
    fun create(todoRequest: TodoRequest, member: Member) = GitIssue(
        content = todoRequest.content,
        memo = todoRequest.memo!!,
        tag = todoRequest.tag,
        deadline = todoRequest.deadline,
        member = member
    )
}