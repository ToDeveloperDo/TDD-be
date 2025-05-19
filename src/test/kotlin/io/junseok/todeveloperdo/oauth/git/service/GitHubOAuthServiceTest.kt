package io.junseok.todeveloperdo.oauth.git.service

import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.oauth.git.client.GitHubAccessTokenClient
import io.junseok.todeveloperdo.oauth.git.client.GitHubApiClient
import io.junseok.todeveloperdo.oauth.git.dto.response.TokenResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.toGitUserResponse
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs

class GitHubOAuthServiceTest : FunSpec({
    val accessTokenClient = mockk<GitHubAccessTokenClient>()
    val gitHubApiClient = mockk<GitHubApiClient>()
    val memberService = mockk<MemberService>()
    val gitHubOAuthService = GitHubOAuthService(
        accessTokenClient,
        gitHubApiClient,
        memberService,
        clientId = "fake-client-id",
        clientSecret = "fake-client-secret"
    )

    test("정상적인 GitHub OAuth 플로우를 처리해야 한다") {
        val code = "auth-code"
        val userName = "testuser"
        val accessTokenRaw = "access_token=abc123&scope=repo"
        val accessToken = "abc123"
        val bearerToken = "Bearer abc123"
        val userInfoJson = """{"id": 1, "login": "tester", "email": "test@example.com"}"""
        val tokenResponse = createGitTokenResponse()
        val expectedParsedMap = mapOf(
            "id" to 1,
            "login" to "tester",
            "email" to "test@example.com"
        )

        every {
            accessTokenClient.getAccessToken(
                "fake-client-id",
                "fake-client-secret",
                code
            )
        } returns accessTokenRaw
        every { gitHubApiClient.getUserInfo(bearerToken) } returns userInfoJson
        every {
            memberService.createGitMember(
                expectedParsedMap.toGitUserResponse(),
                accessToken,
                userName
            )
        } just runs

        val result = gitHubOAuthService.processGitHubOAuth(code, userName)

        result shouldBe tokenResponse
    }

    test("accessToken 응답에 error가 포함되어 있으면 예외가 발생해야 한다") {
        every {
            accessTokenClient.getAccessToken(
                any(),
                any(),
                any()
            )
        } returns "error=bad_verification_code"

        throwsWith<IllegalArgumentException>(
            {
                gitHubOAuthService.processGitHubOAuth("bad-code", "user")
            },
            {
                ex -> ex.message shouldBe "Error response from GitHub: error=bad_verification_code"
            }
        )
    }

    test("정상적인 access_token 응답에서 토큰을 추출해야 한다") {
        val response = "access_token=abc123&scope=repo&token_type=bearer"
        val result = gitHubOAuthService.extractAccessToken(response)
        result shouldBe "abc123"
    }

    test("access_token이 응답 중간에 있어도 정확히 추출해야 한다") {
        val response = "scope=repo&access_token=abc123&token_type=bearer"
        gitHubOAuthService.extractAccessToken(response) shouldBe "abc123"
    }


    test("access_token이 없는 응답이면 예외를 던져야 한다") {
        val response = "error=invalid&scope=repo"
        throwsWith<IllegalArgumentException>(
            {
            gitHubOAuthService.extractAccessToken(response)
            },
            {
                it.message shouldBe "Access token not found or malformed: $response"
            }
        )
    }

    test("access_token 응답이 key=value 형식이 아니면 예외가 발생해야 한다") {
        val malformed = "access_token"

        throwsWith<IllegalArgumentException>({
            gitHubOAuthService.extractAccessToken(malformed)
        }, {
            it.message shouldBe "Access token not found or malformed: $malformed"
        })
    }

    test("access_token= 형식이나 값이 없으면 takeIf에서 null이 되어 예외 발생") {
        val response = "access_token=&scope=repo"

        throwsWith<IllegalArgumentException>({
            gitHubOAuthService.extractAccessToken(response)
        }) {
            it.message shouldBe "Access token not found or malformed: $response"
        }
    }


})

fun createGitTokenResponse() = TokenResponse(token = "Bearer abc123")
