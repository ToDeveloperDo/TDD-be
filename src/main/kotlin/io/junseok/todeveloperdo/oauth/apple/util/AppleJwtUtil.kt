package io.junseok.todeveloperdo.oauth.apple.util

import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import io.junseok.todeveloperdo.oauth.apple.dto.response.ApplePublicKey
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

object AppleJwtUtil {

    fun decodeAndVerify(idToken: String, applePublicKeys: List<ApplePublicKey>): DecodedJWT {
        val kid = JWT.decode(idToken).keyId
        val applePublicKey = applePublicKeys.find { it.kid == kid } ?: throw RuntimeException("Public key not found")

        val nBytes = Base64.getUrlDecoder().decode(applePublicKey.n)
        val eBytes = Base64.getUrlDecoder().decode(applePublicKey.e)

        val n = BigInteger(1, nBytes)
        val e = BigInteger(1, eBytes)

        val publicKeySpec = RSAPublicKeySpec(n, e)
        val keyFactory = KeyFactory.getInstance("RSA")
        val rsaPublicKey = keyFactory.generatePublic(publicKeySpec) as RSAPublicKey

        val algorithm = com.auth0.jwt.algorithms.Algorithm.RSA256(rsaPublicKey, null)
        val verifier = JWT.require(algorithm).build()

        return try {
            verifier.verify(idToken)
        } catch (e: JWTVerificationException) {
            throw RuntimeException("Invalid ID token: ${e.message}", e)
        }
    }

    fun getPayload(idToken: String, applePublicKeys: List<ApplePublicKey>): Map<String, Any> {
        val jwt = decodeAndVerify(idToken, applePublicKeys)
        return jwt.claims.mapValues { it.value.asString() }
    }
}
