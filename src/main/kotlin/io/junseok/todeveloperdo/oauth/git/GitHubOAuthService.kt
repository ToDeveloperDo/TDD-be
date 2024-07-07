package io.junseok.todeveloperdo.oauth.git

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.junseok.todeveloperdo.auth.jwt.TokenProvider
import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.oauth.toGitUserResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service

@Service
class GitHubOAuthService(
    private val gitHubClient: GitHubClient,
    private val gitHubApiClient: GitHubApiClient,
    private val tokenProvider: TokenProvider,
    private val memberService: MemberService
) {

    @Value("\${github.client-id}")
    private lateinit var clientId: String

    @Value("\${github.client-secret}")
    private lateinit var clientSecret: String

    fun processGitHubOAuth(code: String): String {
        val accessTokenResponse = gitHubClient.getAccessToken(clientId, clientSecret, code)

        if (accessTokenResponse.contains("error")) {
            throw IllegalArgumentException("Error response from GitHub: $accessTokenResponse")
        }

        val accessToken = extractAccessToken(accessTokenResponse)
        val bearerToken = "Bearer $accessToken"
        val userInfoResponse = gitHubApiClient.getUserInfo(bearerToken)

        val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userInfo = parseUserInfo(userInfoResponse)
        memberService.createMember(userInfo.toGitUserResponse())
        val user = User(userInfo["login"].toString(), "", authorities)
        val authentication = UsernamePasswordAuthenticationToken(user, null, authorities)

        // JWT 발급
        val jwtToken = tokenProvider.createToken(authentication)
        println("User Info Response: $userInfoResponse")
        return jwtToken
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
