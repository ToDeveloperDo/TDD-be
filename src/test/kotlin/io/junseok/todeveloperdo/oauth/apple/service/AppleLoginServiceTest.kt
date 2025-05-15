package io.junseok.todeveloperdo.oauth.apple.service

import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.oauth.apple.client.AppleClient
import io.junseok.todeveloperdo.oauth.apple.dto.response.*
import io.junseok.todeveloperdo.oauth.apple.service.serviceimpl.ClientSecretCreator
import io.junseok.todeveloperdo.oauth.apple.util.AppleJwtUtil
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*

class AppleLoginServiceTest : FunSpec({

    val appleClient = mockk<AppleClient>()
    val appleMemberService = mockk<AppleMemberService>()
    val tokenProvider = mockk<TokenProvider>()
    val clientSecretCreator = mockk<ClientSecretCreator>()

    val clientId = "dummy-client-id"
    val redirectUrl = "https://example.com/oauth/callback"
    val grantType = "refresh_token"

    val appleLoginService = AppleLoginService(
        appleClient = appleClient,
        appleMemberService = appleMemberService,
        tokenProvider = tokenProvider,
        clientSecretCreator = clientSecretCreator,
        clientId = clientId,
        redirectUrl = redirectUrl,
        grantType = grantType
    )

    test("유효한 인증 코드를 processAppleOAuth()에 넘기면 애플 소셜로그인이 정상적으로 진행된다.") {
        val appleTokenResponse = createAppleTokenResponse()
        val idToken = appleTokenResponse.idToken
        val code = "code"
        val clientSecret = "clientSecret"
        val clientToken = "clientToken"
        val dummyJwt = "header.payload.signature"
        val expectedResponse = TokenResponse(dummyJwt, appleTokenResponse.refreshToken!!)

        val publicKeyResponse = listOf(createApplePublicKey())

        mockkObject(AppleJwtUtil)
        every { AppleJwtUtil.getPayload(idToken, publicKeyResponse) } returns mapOf(
            "sub" to "apple-user-id",
            "email" to "user@example.com"
        )
        every {
            appleClient.getToken(
                clientId = clientId,
                redirectUri = redirectUrl,
                grantType = grantType,
                code = code,
                clientSecret = clientSecret
            )
        } returns appleTokenResponse

        every { clientSecretCreator.createClientSecret() } returns clientSecret
        every { appleClient.getApplePublicKeys().keys } returns publicKeyResponse
        every { tokenProvider.createToken(any()) } returns dummyJwt
        every { appleMemberService.createOrUpdateMember(any(), any(), any(), any()) } just runs

        val result = appleLoginService.processAppleOAuth(code, clientToken)

        result shouldBe expectedResponse
    }

    test("유효하지 않은 idToken이면 예외가 발생해야 한다") {
        val clientSecret = "clientSecret"
        val code = "invalidCode"
        val clientToken = "clientToken"

        val invalidTokenResponse = createAppleTokenResponse()
        val idToken = invalidTokenResponse.idToken
        val publicKeyResponse = listOf(createApplePublicKey())

        every { clientSecretCreator.createClientSecret() } returns clientSecret
        every {
            appleClient.getToken(
                clientId = clientId,
                redirectUri = redirectUrl,
                grantType = grantType,
                code = code,
                clientSecret = clientSecret
            )
        } returns invalidTokenResponse
        every { appleClient.getApplePublicKeys().keys } returns publicKeyResponse

        mockkObject(AppleJwtUtil)
        every {
            AppleJwtUtil.getPayload(
                idToken,
                publicKeyResponse
            )
        } throws RuntimeException("invalid token")

        throwsWith<RuntimeException>(
            {
                appleLoginService.processAppleOAuth(code, clientToken)
            },
            { ex ->
                ex.message shouldBe "invalid token"
            }
        )
    }

    test("refreshAppleToken()를 호출하면 AppleRefreshToken이 재발급된다.") {
        val appleTokenResponse = createAppleTokenResponse()
        val idToken = appleTokenResponse.idToken
        val clientSecret = "clientSecret"
        val publicKeyResponse = listOf(createApplePublicKey())
        val idTokenResponse = createIdTokenResponse()

        mockkObject(AppleJwtUtil)
        every { AppleJwtUtil.getPayload(idToken, publicKeyResponse) } returns mapOf(
            "sub" to "apple-user-id",
            "email" to "user@example.com"
        )
        every {
            appleClient.getApplePublicKeys()
        } returns ApplePublicKeys(publicKeyResponse)
        every { clientSecretCreator.createClientSecret() } returns clientSecret
        every {
            appleClient.refreshToken(
                clientId = clientId,
                grantType = grantType,
                refreshToken = appleTokenResponse.refreshToken!!,
                clientSecret = clientSecret
            )
        } returns appleTokenResponse
        every { tokenProvider.createToken(any()) } returns appleTokenResponse.idToken

        val result =
            appleLoginService.refreshAppleToken(appleTokenResponse.refreshToken!!)

        result shouldBe idTokenResponse
    }
})

fun createAppleTokenResponse(): AppleTokenResponse {
    val validIdToken =
        "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzRhYmNkIn0." +
                "eyJzdWIiOiJhcHBsZS11c2VyLWlkIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIn0." +
                "signature"
    return AppleTokenResponse(
        accessToken = validIdToken,
        tokenType = "tokenType",
        expiresIn = 10,
        refreshToken = validIdToken,
        idToken = validIdToken
    )
}

fun createApplePublicKey() = ApplePublicKey(
    kid = "1234abcd",
    alg = "RS256",
    use = "sig",
    kty = "RSA",
    n = "AKK6zUxz1RHfNi9v4k6Gm2ep3HDYFQQYvESKnGbzZSo6gC1mYOEBZkpQa0ZBtb-93zzxErBHpvh8g2o_hEylgJXvfx0mvQdJ_HGe__LfJYZzY4U0HZftD1PVQ5Ku7vAcmb9OyVXl77lRQJojMyEo6Vz5_6GpPaNwO2-lzvH4sNCf",
    e = "AQAB"
)

fun createIdTokenResponse(): IdTokenResponse {
    val validIdToken =
        "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEyMzRhYmNkIn0." +
                "eyJzdWIiOiJhcHBsZS11c2VyLWlkIiwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIn0." +
                "signature"
    return IdTokenResponse(
        idToken = validIdToken
    )
}

