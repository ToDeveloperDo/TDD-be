package io.junseok.todeveloperdo.oauth.git.client

import io.junseok.todeveloperdo.oauth.git.config.FeignConfig
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssueStateRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubIssueResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "githubIssuesClient",
    url = "https://api.github.com",
    configuration = [FeignConfig::class]
)
interface GitHubIssuesClient {
    @PostMapping("/repos/{owner}/{repo}/issues")
    fun createIssue(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody issue: GitHubIssuesRequest
    ): GitHubIssueResponse

    @PatchMapping("/repos/{owner}/{repo}/issues/{issue_number}")
    fun issueStateUpdate(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("issue_number") issueNumber: Int,
        @RequestBody issueClose: GitHubIssueStateRequest
    ): GitHubIssueResponse

    @PatchMapping("/repos/{owner}/{repo}/issues/{issue_number}")
    fun updateIssue(
        @RequestHeader("Authorization") token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("issue_number") issueNumber: Int,
        @RequestBody issue: GitHubIssuesRequest
    ): GitHubIssueResponse
}