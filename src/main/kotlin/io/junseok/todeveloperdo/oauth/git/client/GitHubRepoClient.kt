package io.junseok.todeveloperdo.oauth.git.client

import io.junseok.todeveloperdo.oauth.git.config.GitHubRepoConfig
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubRepoResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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
        @RequestHeader("Authorization") token: String,
        @RequestBody body: Map<String, Any>
    ): Map<String, Any>

    @GetMapping("/repos/{owner}/{repo}")
    fun isExistRepo(
        @PathVariable("owner") owner: String,
        @PathVariable("repo") repo: String,
        @RequestHeader("Authorization") token: String
    ): GitHubRepoResponse
}