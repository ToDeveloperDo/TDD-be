package io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl

import io.junseok.todeveloperdo.oauth.git.client.GitHubIssuesClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueStateRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubIssueResponse
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class GitHubIssueProcessor(private val gitHubIssuesClient: GitHubIssuesClient) {
    /**
     * 이슈 생성
     */
    fun createIssue(
        token: String,
        owner: String,
        repo: String,
        issueRequest: GitHubIssuesRequest
    ): GitHubIssueResponse {
        val bearerToken = token.toGeneratorBearerToken()
        val encodedRepo = URLEncoder.encode(repo, "UTF-8")
        return gitHubIssuesClient.createIssue(bearerToken, owner, encodedRepo, issueRequest)
    }

    /**
     * 이슈 close
     */
    fun closeIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: Int,
        gitHubIssueStateRequest: GitHubIssueStateRequest
    ): GitHubIssueResponse {
        val bearerToken = token.toGeneratorBearerToken()
        return gitHubIssuesClient.issueStateUpdate(
            bearerToken,
            owner,
            repo,
            issueNumber,
            gitHubIssueStateRequest
        )
    }

    fun updateIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: Int,
        issueRequest: GitHubIssuesRequest
    ): GitHubIssueResponse {
        return gitHubIssuesClient.updateIssue(token, owner, repo, issueNumber, issueRequest)
    }
}