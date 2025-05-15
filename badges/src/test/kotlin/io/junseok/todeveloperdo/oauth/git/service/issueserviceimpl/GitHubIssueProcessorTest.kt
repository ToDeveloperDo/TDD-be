package io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl

import io.junseok.todeveloperdo.oauth.git.client.GitHubIssuesClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueStateRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.scheduler.createGitIssueResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.net.URLEncoder

class GitHubIssueProcessorTest : FunSpec({
    val gitHubIssuesClient = mockk<GitHubIssuesClient>()
    val gitHubIssueProcessor = GitHubIssueProcessor(gitHubIssuesClient)

    test("createIssue()가 정상적으로 실행되면 이슈가 생성되어야한다.") {
        val owner = "owner"
        val token = "token"
        val bearerToken = token.toGeneratorBearerToken()
        val repo = "repo"
        val gitIssueRequest = createGitIssueRequest()
        val encodeRepo = URLEncoder.encode(repo, "UTF-8")
        val gitIssueResponse = createGitIssueResponse()
        every {
            gitHubIssuesClient.createIssue(
                bearerToken,
                owner,
                encodeRepo,
                gitIssueRequest
            )
        } returns gitIssueResponse

        val result = gitHubIssueProcessor.createIssue(
            "token",
            owner,
            repo,
            gitIssueRequest
        )

        result shouldBe gitIssueResponse
    }

    test("closeIssue()를 호출하면 이슈가 close 상태가 되어야한다.") {
        val owner = "owner"
        val token = "token"
        val bearerToken = token.toGeneratorBearerToken()
        val repo = "repo"
        val issueNumber = 1
        val gitIssueResponse = createGitIssueResponse()
        val gitHubIssueStateRequest = createGitHubIssueStateRequest()

        every {
            gitHubIssuesClient.issueStateUpdate(
                bearerToken,
                owner,
                repo,
                issueNumber,
                gitHubIssueStateRequest
            )
        } returns gitIssueResponse

        val result = gitHubIssueProcessor.closeIssue(
            token,
            owner,
            repo,
            issueNumber,
            gitHubIssueStateRequest
        )

        result shouldBe gitIssueResponse
    }

    test("updateIssue()를 호출하면 이슈가 수정되어야한다.") {
        val owner = "owner"
        val token = "token"
        val bearerToken = token.toGeneratorBearerToken()
        val repo = "repo"
        val issueNumber = 1
        val gitIssueResponse = createGitIssueResponse()
        val gitIssueRequest = createGitIssueRequest()

        every {
            gitHubIssuesClient.updateIssue(
                bearerToken,
                owner,
                repo,
                issueNumber,
                gitIssueRequest
            )
        } returns gitIssueResponse

        val result = gitHubIssueProcessor.updateIssue(
            bearerToken,
            owner,
            repo,
            issueNumber,
            gitIssueRequest
        )

        result shouldBe gitIssueResponse
    }
})

fun createGitIssueRequest() = GitHubIssuesRequest(
    title = "title",
    body = "body",
    assignees = listOf("assignees"),
    labels = listOf("labels")
)

fun createGitHubIssueStateRequest() = GitHubIssueStateRequest(state = "state")