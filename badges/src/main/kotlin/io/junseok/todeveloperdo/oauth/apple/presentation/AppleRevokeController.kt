package io.junseok.todeveloperdo.oauth.apple.presentation

import io.junseok.todeveloperdo.oauth.apple.service.AppleMemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/apple")
@CrossOrigin
class AppleRevokeController(private val appleMemberService: AppleMemberService) {

    @PostMapping
    fun revokeMember(principal: Principal): ResponseEntity<Any> =
        ResponseEntity.ok(appleMemberService.revoke(principal.name))

}