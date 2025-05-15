package io.junseok.todeveloperdo.event.issue.dto.request

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import java.util.concurrent.CompletableFuture

data class IssueEventRequest(
    val member: Member,
    val todoRequest: TodoRequest,
    val issueNumber: CompletableFuture<Int> = CompletableFuture()
)