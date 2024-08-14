package io.junseok.todeveloperdo.oauth.apple.service

import feign.FeignException
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.dto.response.AppleTokenResponse
import io.junseok.todeveloperdo.oauth.apple.dto.response.IdTokenResponse
import io.junseok.todeveloperdo.oauth.apple.dto.response.TokenResponse
import io.junseok.todeveloperdo.oauth.apple.service.serviceimpl.ClientSecretCreator
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import io.junseok.todeveloperdo.oauth.git.service.CustomOAuth2UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class AppleLoginService(
    private val appleClient: AppleClient,
    private val appleMemberService: AppleMemberService,
    private val tokenProvider: TokenProvider,
    private val clientSecretCreator: ClientSecretCreator,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,

    @Value("\${spring.security.oauth2.client.registration.apple.redirect-uri}")
    private val redirectUrl: String,

    @Value("\${spring.security.oauth2.client.registration.apple.authorization-grant-type}")
    private val grantType: String,


    ) {

    private val logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)
    fun processAppleOAuth(code: String): TokenResponse {
        val clientSecret = clientSecretCreator.createClientSecret()
        val tokenResponse = getAppleToken(code, clientSecret)
        val idToken = tokenResponse.idToken //access token

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

        val applePublicKeys = appleClient.getApplePublicKeys().keys
        val payload = AppleJwtUtil.getPayload(idToken, applePublicKeys)

        val email = payload["email"] as String
        val userIdentifier = payload["sub"] as String
        val user = User(userIdentifier, "", authorities)
        val authentication = UsernamePasswordAuthenticationToken(user, null, authorities)
        val jwtToken = tokenProvider.createToken(authentication)
        appleMemberService.createOrUpdateMember(userIdentifier, email, tokenResponse.refreshToken!!)
        return TokenResponse(
            idToken = jwtToken,
            refreshToken = tokenResponse.refreshToken
        )
    }

    private fun getAppleToken(code: String, clientSecret: String): AppleTokenResponse {
        return appleClient.getToken(clientId, redirectUrl, grantType, code, clientSecret)
    }

    fun refreshAppleToken(refreshToken: String): IdTokenResponse? {

        return try {
            val response = appleClient.refreshToken(
                clientId = clientId,
                grantType = "refresh_token",
                refreshToken = refreshToken,
                clientSecret = clientSecretCreator.createClientSecret()
            )
            val idTokenResponse = IdTokenResponse(idToken = response.idToken)
            logger.info(idTokenResponse.idToken)
            idTokenResponse
        } catch (e: FeignException) {
            // Apple에서 'invalid_grant' 오류가 발생하면 refresh_token이 만료된 것으로 간주
            if (e.status() == 400 && e.contentUTF8().contains("invalid_grant")) {
                // 처리 방법: 사용자에게 다시 로그인 요구, 오류 로그 기록 등
                logger.error("Refresh token has expired or is invalid. Please re-authenticate.")
                null
            } else {
                throw e
            }
        }
    }

    companion object {
        const val REFRESH_GRANT_TYPE = "refresh_token"
    }
}
