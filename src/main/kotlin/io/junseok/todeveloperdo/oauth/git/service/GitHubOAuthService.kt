package io.junseok.todeveloperdo.oauth.git.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.oauth.git.client.GitHubAccessTokenClient
import io.junseok.todeveloperdo.oauth.git.client.GitHubApiClient
import io.junseok.todeveloperdo.oauth.git.dto.response.TokenResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.toGitUserResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GitHubOAuthService(
    private val accessTokenClient: GitHubAccessTokenClient,
    private val gitHubApiClient: GitHubApiClient,
    private val memberService: MemberService
) {

    @Value("\${spring.security.oauth2.client.registration.github.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.github.client-secret}")
    private lateinit var clientSecret: String

    fun processGitHubOAuth(code: String,userName: String): TokenResponse {
        val accessTokenResponse = accessTokenClient.getAccessToken(clientId, clientSecret, code)

        if (accessTokenResponse.contains("error")) {
            throw IllegalArgumentException("Error response from GitHub: $accessTokenResponse")
        }

        val accessToken = extractAccessToken(accessTokenResponse)
        val bearerToken = "Bearer $accessToken"
        val userInfoResponse = gitHubApiClient.getUserInfo(bearerToken)

        val userInfo = parseUserInfo(userInfoResponse)
        memberService.createGitMember(userInfo.toGitUserResponse(),accessToken,userName)
        return TokenResponse(bearerToken)
    }

    private fun extractAccessToken(response: String): String {
        return response.split("&")
            .firstOrNull { it.startsWith("access_token") }
            ?.split("=")?.get(1)
            ?: throw IllegalArgumentException("Access token not found in response: $response")
    }

    private fun parseUserInfo(response: String): Map<String, Any> {
        val mapper = jacksonObjectMapper()
        return mapper.readValue(response, object : TypeReference<Map<String, Any>>() {})
    }
}
