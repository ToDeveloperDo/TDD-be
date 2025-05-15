package io.junseok.todeveloperdo.event.issue.listener

import io.junseok.todeveloperdo.domains.gitissue.toCreateIssueTemplate
import io.junseok.todeveloperdo.event.issue.dto.request.IssueCloseEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueUpdateEventRequest
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class TodoIssueCreateEventListener(
    private val gitHubIssueProcessor: GitHubIssueProcessor,
) {

    @EventListener
    fun create(issueEventRequest: IssueEventRequest) {
        val gitHubIssuesRequest =
            issueEventRequest.todoRequest.toTodoCreate(issueEventRequest.member)
                .toCreateIssueTemplate()

        // 이슈 생성
        val createIssue = gitHubIssueProcessor.createIssue(
            issueEventRequest.member.gitHubToken!!,
            issueEventRequest.member.gitHubUsername!!,
            issueEventRequest.member.gitHubRepo!!,
            gitHubIssuesRequest
        )
        issueEventRequest.issueNumber.complete(createIssue.number)
    }

    @EventListener
    fun close(issueCloseEventRequest: IssueCloseEventRequest) {
        gitHubIssueProcessor.closeIssue(
            issueCloseEventRequest.member.gitHubToken!!,
            issueCloseEventRequest.member.gitHubUsername!!,
            issueCloseEventRequest.member.gitHubRepo!!,
            issueCloseEventRequest.issueNumber,
            issueCloseEventRequest.gitHubIssueStateRequest
        )
    }

    @EventListener
    fun update(issueUpdateEventRequest: IssueUpdateEventRequest) {
        gitHubIssueProcessor.updateIssue(
            issueUpdateEventRequest.member.gitHubToken!!.toGeneratorBearerToken(),
            issueUpdateEventRequest.member.gitHubUsername!!,
            issueUpdateEventRequest.member.gitHubRepo!!,
            issueUpdateEventRequest.issueNumber,
            issueUpdateEventRequest.todoCreate.toCreateIssueTemplate()
        )
    }
}