package io.junseok.todeveloperdo.oauth.git.client

import io.junseok.todeveloperdo.oauth.git.config.GitHubClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "githubApiClient",
    url = "https://api.github.com",
    configuration = [GitHubClientConfig::class]
)
interface GitHubApiClient {
    @GetMapping("/user")
    fun getUserInfo(): String
}
