package io.junseok.todeveloperdo.oauth.apple

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.dto.response.AppleTokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*
import javax.xml.bind.DatatypeConverter

@Service
class AppleLoginService(
    private val appleClient: AppleClient,
    private val appleMemberService: AppleMemberService,
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,

    @Value("\${apple.team-id}")
    private val teamId: String,

    @Value("\${apple.key-id}")
    private val keyId: String,

    @Value("\${apple.private-key}")
    private val privateKey: String
) {


    fun processAppleOAuth(code: String): String {
        val clientSecret = createClientSecret()
        val tokenResponse = getAppleToken(code, clientSecret)
        val idToken = tokenResponse.id_token
        val applePublicKeys = appleClient.getApplePublicKeys().keys
        val payload = AppleJwtUtil.getPayload(idToken, applePublicKeys)

        val email = payload["email"] as String
        val userIdentifier = payload["sub"] as String

        appleMemberService.createOrUpdateMember(userIdentifier, email)

        return idToken
    }

    private fun getAppleToken(code: String, clientSecret: String): AppleTokenResponse {
        val formData = LinkedMultiValueMap<String, String>()
        formData.add("client_id", clientId)
        formData.add("client_secret", clientSecret)
        formData.add("grant_type", "authorization_code")
        formData.add("code", code)

        return appleClient.getToken(formData)
    }

    private fun createClientSecret(): String {
        val now = System.currentTimeMillis()
        val expiration = Date(now + 3600000) // 1시간 동안 유효

        val privateKey = System.getenv("APPLE_PRIVATE_KEY")
        val keyFactory = KeyFactory.getInstance("EC")

        // Base64 디코딩
        val keyBytes = Base64.getDecoder().decode(privateKey)
        val pkcs8EncodedKeySpec =
            PKCS8EncodedKeySpec(keyBytes)
        val privateKeyObject = keyFactory.generatePrivate(pkcs8EncodedKeySpec)

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
}
