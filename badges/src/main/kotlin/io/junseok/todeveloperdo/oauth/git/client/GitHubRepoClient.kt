package io.junseok.todeveloperdo.oauth.git.client

import io.junseok.todeveloperdo.client.openai.config.OpenChatAiConfig.Companion.AUTHORIZATION
import io.junseok.todeveloperdo.oauth.git.config.GitHubRepoConfig
import io.junseok.todeveloperdo.oauth.git.domain.GItHubRepo
import io.junseok.todeveloperdo.oauth.git.dto.request.WebhookRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.WebhookResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "githubUserClient",
    url = "https://api.github.com",
    configuration = [GitHubRepoConfig::class]
)
interface GitHubRepoClient {
    @PostMapping(value = ["/user/repos"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createRepository(
        @RequestHeader(AUTHORIZATION) token: String,
        @RequestBody body: GItHubRepo
    ): GitHubResponse

/*    @GetMapping("/repos/{owner}/{repo}")
    fun isExistRepo(
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestHeader(AUTHORIZATION) token: String
    ): GitHubRepoResponse*/

    @PostMapping("/repos/{owner}/{repo}/hooks")
    fun createWebhook(
        @RequestHeader(AUTHORIZATION) token: String,
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestBody webhookRequest: WebhookRequest
    ): WebhookResponse
}