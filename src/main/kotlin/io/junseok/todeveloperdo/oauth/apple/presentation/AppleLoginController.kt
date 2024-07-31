package io.junseok.todeveloperdo.oauth.apple.presentation

import io.junseok.todeveloperdo.oauth.apple.dto.request.RefreshTokenRequest
import io.junseok.todeveloperdo.oauth.apple.dto.response.IdTokenResponse
import io.junseok.todeveloperdo.oauth.apple.service.AppleLoginService
import io.junseok.todeveloperdo.oauth.apple.dto.response.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/login")
class AppleLoginController(
    private val appleLoginService: AppleLoginService
) {
    @PostMapping("/apple")
    fun appleLogin(@RequestParam code: String): ResponseEntity<TokenResponse> =
        ResponseEntity.ok(appleLoginService.processAppleOAuth(code))

    @PostMapping("/refresh")
    fun reLogin(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<IdTokenResponse> =
        ResponseEntity.ok(appleLoginService.refreshAppleToken(refreshTokenRequest.refreshToken))
}