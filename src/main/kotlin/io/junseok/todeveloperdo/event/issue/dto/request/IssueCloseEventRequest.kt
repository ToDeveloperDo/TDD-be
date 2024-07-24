package io.junseok.todeveloperdo.event.issue.dto.request

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueStateRequest

data class IssueCloseEventRequest(
    val member: Member,
    val issueNumber: Int,
    val gitHubIssueStateRequest: GitHubIssueStateRequest
)
