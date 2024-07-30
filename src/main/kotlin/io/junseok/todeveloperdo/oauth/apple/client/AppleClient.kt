package io.junseok.todeveloperdo.oauth.apple.client

import feign.Headers
import io.junseok.todeveloperdo.oauth.apple.config.AppleConfig
import io.junseok.todeveloperdo.oauth.apple.dto.response.ApplePublicKeys
import io.junseok.todeveloperdo.oauth.apple.dto.response.AppleTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "appleClient", url = "https://appleid.apple.com",configuration = [AppleConfig::class])
interface AppleClient {
    @PostMapping(value = ["/auth/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun getToken(@RequestParam("client_id") clientId:String,
                 @RequestParam("redirect_uri") redirectUri: String,
                 @RequestParam("grant_type") grantType: String,
                 @RequestParam("code") code: String,
                 @RequestParam("client_secret") clientSecret: String): AppleTokenResponse

    @GetMapping("/auth/keys")
    fun getApplePublicKeys(): ApplePublicKeys
}