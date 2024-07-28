package io.junseok.todeveloperdo.oauth.apple

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/login")
class AppleLoginController(
    private val appleLoginService: AppleLoginService
) {

    @PostMapping("/apple")
    fun appleLogin(@RequestParam code: String): ResponseEntity<String> {
        val tokenResponse = appleLoginService.processAppleOAuth(code)
        return ResponseEntity.ok(tokenResponse)
    }
}
