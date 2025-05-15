package io.junseok.todeveloperdo.event.issue

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.event.issue.dto.request.IssueCloseEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueUpdateEventRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueStateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class IssueEventProcessor(
    private val eventPublisher: ApplicationEventPublisher,
) {
    fun create(member: Member, todoRequest: TodoRequest): IssueEventRequest {
        val issueEventRequest = IssueEventRequest(member, todoRequest)
        eventPublisher.publishEvent(issueEventRequest)
        return issueEventRequest
    }
    fun close(member: Member, issueNumber: Int, state: String) {
        val issueCloseEventRequest = IssueCloseEventRequest(
            member,
            issueNumber,
            GitHubIssueStateRequest(state = state)
        )
        eventPublisher.publishEvent(issueCloseEventRequest)
    }
    fun update(
        member: Member,
        issueNumber: Int,
        todoCreate: TodoCreate
    ) {
        val issueUpdateEventRequest = IssueUpdateEventRequest(
            member,
            issueNumber,
            todoCreate
        )
        eventPublisher.publishEvent(issueUpdateEventRequest)
    }
}