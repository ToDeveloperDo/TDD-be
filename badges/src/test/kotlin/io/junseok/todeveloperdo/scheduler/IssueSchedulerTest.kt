package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueReader
import io.junseok.todeveloperdo.domains.gitissue.toCreateIssueTemplate
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubIssueResponse
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueCreator
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class IssueSchedulerTest : FunSpec({
    val gitIssueReader = mockk<GitIssueReader>()
    val gitHubIssueCreator = mockk<GitHubIssueCreator>()
    val gitHubIssueProcessor = mockk<GitHubIssueProcessor>()

    val issueScheduler = IssueScheduler(
        gitIssueReader,
        gitHubIssueCreator,
        gitHubIssueProcessor
    )

    test("이슈 생성") {
        val member = createMember(1, "appleId","repo")
        val todoRequest = createTodoRequest()
        val todoCreates = listOf(todoRequest.toTodoCreate(member))
        val issueTemplate = todoCreates[0].toCreateIssueTemplate()
        val gitIssueResponse = createGitIssueResponse()

        every { gitIssueReader.findGitIssueList() } returns todoCreates
        todoCreates.forEach {
            every { gitHubIssueCreator.createIssueTemplate(it) } returns issueTemplate
            every {
                gitHubIssueProcessor.createIssue(
                    member.gitHubToken!!,
                    member.gitHubUsername!!,
                    member.gitHubRepo!!,
                    issueTemplate
                )
            } returns gitIssueResponse
        }
        issueScheduler.makeIssue()

        verify(exactly = 1) { gitIssueReader.findGitIssueList() }
        todoCreates.forEach {
            verify { gitHubIssueCreator.createIssueTemplate(it) }
            verify {
                gitHubIssueProcessor.createIssue(
                    member.gitHubToken!!,
                    member.gitHubUsername!!,
                    member.gitHubRepo!!,
                    issueTemplate
                )
            }
        }
    }
})

fun createGitIssueResponse() = GitHubIssueResponse(
    id = 1,
    url = "url",
    title = "title",
    body = "body",
    state = "state",
    number = 1
)