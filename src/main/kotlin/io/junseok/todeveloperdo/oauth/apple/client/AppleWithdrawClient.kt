package io.junseok.todeveloperdo.oauth.apple.client

import feign.Headers
import io.junseok.todeveloperdo.oauth.apple.config.AppleConfig
import io.junseok.todeveloperdo.oauth.apple.config.AppleWithdrawConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "appleWithdrawClient",
    url = "https://appleid.apple.com",
    configuration = [AppleWithdrawConfig::class]
)
interface AppleWithdrawClient {

    @PostMapping("/auth/revoke")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun revokeToken(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("token") token: String,
        @RequestParam("token_type_hint") tokenTypeHint: String
    )
}
