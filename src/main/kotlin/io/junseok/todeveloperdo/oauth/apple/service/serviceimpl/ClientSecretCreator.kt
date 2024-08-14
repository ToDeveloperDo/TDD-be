package io.junseok.todeveloperdo.oauth.apple.service.serviceimpl

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

@Component
class ClientSecretCreator(
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,

    @Value("\${apple.team-id}")
    private val teamId: String,

    @Value("\${apple.key-id}")
    private val keyId: String,

    @Value("\${apple.private-key}")
    private val privateKey: String
) {
    fun createClientSecret(): String {
        val now = System.currentTimeMillis()
        val expiration = Date(now + 3600000) // 1시간 동안 유효
        val sanitizedPrivateKey = privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\\r", "")
            .trim()

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
}