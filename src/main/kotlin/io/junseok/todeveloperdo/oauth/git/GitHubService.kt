package io.junseok.todeveloperdo.oauth.git

import io.junseok.todeveloperdo.oauth.git.client.GitHubIssuesClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueUpdateRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubIssueResponse
import org.springframework.stereotype.Service
import java.net.URLEncoder

@Service
class GitHubService(
    private val gitHubIssuesClient: GitHubIssuesClient
) {
    fun createIssue(
        token: String,
        owner: String,
        repo: String,
        issueRequest: GitHubIssuesRequest
    ): GitHubIssueResponse {
        val bearerToken = "Bearer $token"
        val encodedRepo = URLEncoder.encode(repo, "UTF-8")
        return gitHubIssuesClient.createIssue(bearerToken, owner, encodedRepo, issueRequest)
    }

    fun closeIssue(
        token: String,
        owner: String,
        repo: String,
        issueNumber: Int
    ): GitHubIssueResponse {
        val bearerToken = "Bearer $token"
        val issueUpdateRequest = GitHubIssueUpdateRequest(state = "closed")
        return gitHubIssuesClient.closeIssue(bearerToken, owner, repo, issueNumber, issueUpdateRequest)
    }
}