package io.junseok.todeveloperdo.oauth.git.client

import feign.Headers
import io.junseok.todeveloperdo.oauth.git.config.GitHubClientConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "githubClient",
    url = "https://github.com",
    configuration = [GitHubClientConfig::class]
)
interface GitHubAccessTokenClient {
    @PostMapping("/login/oauth/access_token")
    @Headers("Content-Type: application/x-www-form-urlencoded", "Accept: application/json")
    fun getAccessToken(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("code") code: String
    ): String
}