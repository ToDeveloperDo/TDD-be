package io.junseok.todeveloperdo.oauth.apple.client

import io.junseok.todeveloperdo.oauth.apple.dto.response.ApplePublicKeys
import io.junseok.todeveloperdo.oauth.apple.dto.response.AppleTokenResponse
import io.junseok.todeveloperdo.oauth.git.config.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "appleClient", url = "https://appleid.apple.com",configuration = [FeignConfig::class])
interface AppleClient {
    @PostMapping(value = ["/auth/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getToken(@RequestBody formData: MultiValueMap<String, String>): AppleTokenResponse

    @GetMapping("/auth/keys")
    fun getApplePublicKeys(): ApplePublicKeys
}