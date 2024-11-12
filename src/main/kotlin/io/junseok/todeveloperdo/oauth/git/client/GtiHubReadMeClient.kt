package io.junseok.todeveloperdo.oauth.git.client

import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.AUTHORIZATION
import io.junseok.todeveloperdo.oauth.git.config.GitHubRepoConfig
import io.junseok.todeveloperdo.oauth.git.dto.request.FileCommitRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.FileCommitResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.GItHubContent
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubBranchResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(
    name = "githubReadMeClient",
    url = "https://api.github.com",
    configuration = [GitHubRepoConfig::class]
)
interface GtiHubReadMeClient {
    @GetMapping("/repos/{owner}/{repo}/branches")
    fun getBranches(
        @RequestHeader(AUTHORIZATION) token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String
    ): List<GitHubBranchResponse>

    @PutMapping("/repos/{owner}/{repo}/contents/{path}")
    fun createOrUpdateFile(
        @RequestHeader(AUTHORIZATION) token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String,
        @RequestBody body: FileCommitRequest
    ): FileCommitResponse

    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    fun getFile(
        @RequestHeader(AUTHORIZATION) token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @PathVariable("path") path: String
    ): GItHubContent
}