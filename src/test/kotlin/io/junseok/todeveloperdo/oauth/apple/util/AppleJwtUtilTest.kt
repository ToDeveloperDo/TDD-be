package io.junseok.todeveloperdo.oauth.apple.util

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.junseok.todeveloperdo.oauth.apple.dto.response.ApplePublicKey
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

class AppleJwtUtilTest : FunSpec({

    test("kid에 해당하는 공개키가 없으면 예외 발생") {
        val idToken = JWT.create()
            .withKeyId("missing-kid")
            .withSubject("someone")
            .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("secret"))

        val wrongPublicKey = ApplePublicKey(
            kid = "something-else",
            alg = "RS256",
            use = "sig",
            kty = "RSA",
            n = "AAA",
            e = "AQAB"
        )
        throwsWith<RuntimeException>(
            {
                AppleJwtUtil.decodeAndVerify(idToken, listOf(wrongPublicKey))

            },
            { ex ->
                ex.message shouldBe "Public key not found"
            }
        )
    }

    test("정상적인 공개키를 사용하면 JWT 검증에 성공한다") {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.genKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey

        val idToken = JWT.create()
            .withKeyId("test-kid")
            .withSubject("apple-user-id")
            .sign(com.auth0.jwt.algorithms.Algorithm.RSA256(publicKey, privateKey))

        val n = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(publicKey.modulus.toByteArray().checkBytes())
        val e = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(publicKey.publicExponent.toByteArray().checkBytes())

        val applePublicKey = ApplePublicKey(
            kid = "test-kid",
            alg = "RS256",
            use = "sig",
            kty = "RSA",
            n = n,
            e = e
        )

        val decodedJWT = AppleJwtUtil.decodeAndVerify(idToken, listOf(applePublicKey))

        decodedJWT.subject shouldBe "apple-user-id"
    }

    test("getPayload는 DecodedJWT에서 claims만 파싱해야 한다") {
        val jwt = mockk<DecodedJWT>()
        every { jwt.claims } returns mapOf(
            "sub" to mockk { every { asString() } returns "apple-user-id" },
            "email" to mockk { every { asString() } returns "user@example.com" },
            "email_verified" to mockk { every { asString() } returns "true" }
        )

        mockkObject(AppleJwtUtil)
        every { AppleJwtUtil.decodeAndVerify(any(), any()) } returns jwt

        val result = AppleJwtUtil.getPayload("dummy-id-token", emptyList())

        result["sub"] shouldBe "apple-user-id"
        result["email"] shouldBe "user@example.com"
        result["email_verified"] shouldBe "true"
    }

})
fun ByteArray.checkBytes(): ByteArray {
    return this.dropWhile { it == 0.toByte() }.toByteArray()
}

