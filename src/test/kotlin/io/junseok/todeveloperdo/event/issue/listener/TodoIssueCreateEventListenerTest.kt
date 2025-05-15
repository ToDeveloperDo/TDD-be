package io.junseok.todeveloperdo.event.issue.listener

import io.junseok.todeveloperdo.domains.gitissue.toCreateIssueTemplate
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.issue.createIssueCloseEventRequest
import io.junseok.todeveloperdo.event.issue.createIssueEventRequest
import io.junseok.todeveloperdo.event.issue.createIssueUpdateEventRequest
import io.junseok.todeveloperdo.event.issue.createTodoCreate
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.junseok.todeveloperdo.scheduler.createGitIssueResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.concurrent.CompletableFuture

class TodoIssueCreateEventListenerTest : FunSpec({
    val gitHubIssueProcessor = mockk<GitHubIssueProcessor>()
    val listener = TodoIssueCreateEventListener(gitHubIssueProcessor)

    test("IssueEventRequest를 수신하면 create()가 호출되고 issueNumber가 complete 되어야 한다") {
        val member = createMember(1, "appleId", "repo")
        val todoRequest = createTodoRequest()
        val issueFuture = CompletableFuture<Int>().apply { complete(1) }
        val gitIssueResponse = createGitIssueResponse()
        val issueEventRequest =
            createIssueEventRequest(member, todoRequest, issueFuture)

        val issueTemplate = issueEventRequest.todoRequest
            .toTodoCreate(issueEventRequest.member)
            .toCreateIssueTemplate()

        every {
            gitHubIssueProcessor.createIssue(
                issueEventRequest.member.gitHubToken!!,
                issueEventRequest.member.gitHubUsername!!,
                issueEventRequest.member.gitHubRepo!!,
                issueTemplate
            )
        } returns gitIssueResponse

        listener.create(issueEventRequest)

        issueEventRequest.issueNumber.isDone shouldBe true
        issueEventRequest.issueNumber.get() shouldBe gitIssueResponse.number
    }

    test("issueCloseEventRequest를 수신하면 close()가 호출되고 이슈가 닫혀야한다.") {
        val member = createMember(1, "appleId", "repo")
        val issueNumber = 1
        val issueCloseEventRequest = createIssueCloseEventRequest(member, issueNumber)
        val gitIssueResponse = createGitIssueResponse()
        every {
            gitHubIssueProcessor.closeIssue(
                issueCloseEventRequest.member.gitHubToken!!,
                issueCloseEventRequest.member.gitHubUsername!!,
                issueCloseEventRequest.member.gitHubRepo!!,
                issueCloseEventRequest.issueNumber,
                issueCloseEventRequest.gitHubIssueStateRequest
            )
        } returns gitIssueResponse

        listener.close(issueCloseEventRequest)

        verify(exactly = 1) {
            gitHubIssueProcessor.closeIssue(
                eq(member.gitHubToken!!),
                eq(member.gitHubUsername!!),
                eq(member.gitHubRepo!!),
                eq(issueNumber),
                eq(issueCloseEventRequest.gitHubIssueStateRequest)
            )
        }
    }

    test("issueUpdateEventRequest를 수신하면 update()가 호출되고 이슈가 수정된다.") {
        val member = createMember(1, "appleId","repo")
        val issueNumber = 123
        val todoCreate = createTodoCreate(1, member)
        val issueUpdateEventRequest =
            createIssueUpdateEventRequest(member, issueNumber, todoCreate)
        val gitIssueResponse = createGitIssueResponse()

        every {
            gitHubIssueProcessor.updateIssue(
                issueUpdateEventRequest.member.gitHubToken!!.toGeneratorBearerToken(),
                issueUpdateEventRequest.member.gitHubUsername!!,
                issueUpdateEventRequest.member.gitHubRepo!!,
                issueUpdateEventRequest.issueNumber,
                issueUpdateEventRequest.todoCreate.toCreateIssueTemplate()
            )
        } returns gitIssueResponse

        listener.update(issueUpdateEventRequest)

        verify(exactly = 1) {
            gitHubIssueProcessor.updateIssue(
                eq(member.gitHubToken!!.toGeneratorBearerToken()),
                eq(member.gitHubUsername!!),
                eq(member.gitHubRepo!!),
                eq(issueNumber),
                eq(issueUpdateEventRequest.todoCreate.toCreateIssueTemplate())
            )
        }
    }


})
