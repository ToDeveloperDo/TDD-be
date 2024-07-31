package io.junseok.todeveloperdo.oauth.apple.service

import feign.FeignException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.dto.response.AppleTokenResponse
import io.junseok.todeveloperdo.oauth.apple.dto.response.IdTokenResponse
import io.junseok.todeveloperdo.oauth.apple.dto.response.TokenResponse
import io.junseok.todeveloperdo.oauth.git.service.CustomOAuth2UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@Service
class AppleLoginService(
    private val appleClient: AppleClient,
    private val appleMemberService: AppleMemberService,
    private val tokenProvider: TokenProvider,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,

    @Value("\${apple.team-id}")
    private val teamId: String,

    @Value("\${apple.key-id}")
    private val keyId: String,

    @Value("\${apple.private-key}")
    private val privateKey: String,

    @Value("\${spring.security.oauth2.client.registration.apple.redirect-uri}")
    private val redirectUrl: String,

    @Value("\${spring.security.oauth2.client.registration.apple.authorization-grant-type}")
    private val grantType: String,


    ) {

    private val logger = LoggerFactory.getLogger(CustomOAuth2UserService::class.java)
    fun processAppleOAuth(code: String): TokenResponse {
        val clientSecret = createClientSecret()
        val tokenResponse = getAppleToken(code, clientSecret)
        logger.info("토큰 만료시간: ${tokenResponse.expiresIn}")
        val idToken = tokenResponse.idToken
        val applePublicKeys = appleClient.getApplePublicKeys().keys
        val payload = AppleJwtUtil.getPayload(idToken, applePublicKeys)

        val email = payload["email"] as String
        val userIdentifier = payload["sub"] as String

        appleMemberService.createOrUpdateMember(userIdentifier, email,tokenResponse.refreshToken)
        return TokenResponse(
            idToken = idToken,
            refreshToken = tokenResponse.refreshToken
        )
    }

    private fun getAppleToken(code: String, clientSecret: String): AppleTokenResponse {
        return appleClient.getToken(clientId,redirectUrl,grantType,code,clientSecret)
    }

    private fun createClientSecret(): String {
        val now = System.currentTimeMillis()
        val expiration = Date(now + 3600000) // 1시간 동안 유효
        val sanitizedPrivateKey = privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\\r", "")
            .trim()
        println("Sanitized private key: $sanitizedPrivateKey")

        val keyFactory = KeyFactory.getInstance("EC")

        // Base64 디코딩
        val keyBytes = Base64.getDecoder().decode(sanitizedPrivateKey)
        val pkcs8EncodedKeySpec = PKCS8EncodedKeySpec(keyBytes)
        val privateKeyObject = keyFactory.generatePrivate(pkcs8EncodedKeySpec)
        println("decoder Error!!aaaaaaaaaaaa")

        return Jwts.builder()
            .setHeaderParam("kid", keyId)
            .setHeaderParam("alg", "ES256")
            .setIssuer(teamId)
            .setIssuedAt(Date(now))
            .setExpiration(expiration)
            .setAudience("https://appleid.apple.com")
            .setSubject(clientId)
            .signWith(privateKeyObject, SignatureAlgorithm.ES256)
            .compact()
    }

    fun refreshAppleToken(refreshToken: String): IdTokenResponse? {

        return try {
            val response = appleClient.refreshToken(
                clientId = clientId,
                clientSecret = createClientSecret(),
                grantType = "refresh_token",
                refreshToken = refreshToken
            )
            IdTokenResponse(idToken = response.idToken)
        } catch (e: FeignException) {
            // Apple에서 'invalid_grant' 오류가 발생하면 refresh_token이 만료된 것으로 간주
            if (e.status() == 400 && e.contentUTF8().contains("invalid_grant")) {
                // 처리 방법: 사용자에게 다시 로그인 요구, 오류 로그 기록 등
                logger.error("Refresh token has expired or is invalid. Please re-authenticate.")
                null
            }else{
                throw  e
            }
        }


           /* if (tokenProvider.validateAppleToken(refreshToken,"REFRESH")){
        val clientSecret = createClientSecret()
        val tokenResponse =
            appleClient.refreshToken(clientId, REFRESH_GRANT_TYPE, refreshToken, clientSecret)
        return IdTokenResponse(idToken = tokenResponse.idToken)
        }
        return null*/
    }

    companion object {
        const val REFRESH_GRANT_TYPE = "refresh_token"
    }
}
