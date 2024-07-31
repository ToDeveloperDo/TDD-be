package io.junseok.todeveloperdo.auth.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.service.AppleLoginService
import mu.KotlinLogging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component

@Component
class TokenProvider(
    private val appleClient: AppleClient, // AppleClient 주입
    private val appleLoginService: AppleLoginService,
    private val memberRepository: MemberRepository
) {

    val log = KotlinLogging.logger {}

    // 애플 JWT 토큰의 유효성 검증을 수행
    fun validateAppleToken(token: String,type: String): Boolean {
        return try {
            val applePublicKeys = appleClient.getApplePublicKeys().keys
            AppleJwtUtil.decodeAndVerify(token, applePublicKeys)
            true
        } catch (e: ExpiredJwtException) {
            if(type == "REFRESH"){
               if(memberRepository.existsByAppleRefreshToken(token)){
                   memberRepository.deleteByAppleRefreshToken(token)
               }else {
                   log.info("No refresh token found to delete.")
               }
            }
            throw ToDeveloperDoException{ErrorCode.EXPIRED_JWT}
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