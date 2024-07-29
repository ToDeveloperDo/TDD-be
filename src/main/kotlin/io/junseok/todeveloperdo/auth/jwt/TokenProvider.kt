package io.junseok.todeveloperdo.auth.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.EXPIRED_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.MALFORMED_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.UNSUPPORT_JWT
import io.junseok.todeveloperdo.auth.jwt.SecurityMessage.Companion.WRONG_JWT
import io.junseok.todeveloperdo.oauth.apple.AppleJwtUtil
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import mu.KotlinLogging
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(
    private val appleClient: AppleClient // AppleClient 주입
) {

    val log = KotlinLogging.logger {}

    // 애플 JWT 토큰의 유효성 검증을 수행
    fun validateAppleToken(token: String): Boolean {
        return try {
            val applePublicKeys = appleClient.getApplePublicKeys().keys
            AppleJwtUtil.decodeAndVerify(token, applePublicKeys)
            true
        } catch (e: Exception) {
            log.error("Invalid Apple JWT token", e)
            false
        }
    }

    // 애플 JWT로부터 Authentication 객체를 생성
    fun getAppleAuthentication(token: String): Authentication {
        val applePublicKeys = appleClient.getApplePublicKeys().keys
        val decodedJWT = AppleJwtUtil.decodeAndVerify(token, applePublicKeys)
        val payload = AppleJwtUtil.getPayload(token, applePublicKeys)

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val principal = User(payload["sub"] as String, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }
}