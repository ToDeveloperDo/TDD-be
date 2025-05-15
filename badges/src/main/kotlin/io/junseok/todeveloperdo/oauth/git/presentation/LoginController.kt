package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.service.GitHubOAuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin
class LoginController(
    private val gitHubOAuthService: GitHubOAuthService,
    @Value("\${spring.security.oauth2.client.registration.github.client-id}")
    private val clientId: String,
    @Value("\${spring.security.oauth2.client.registration.github.redirect-uri}")
    private val redirectUri: String,
) {
    @GetMapping("/git/login")
    fun redirectToGithub(
        httpServletResponse: HttpServletResponse,
        @RequestParam("appleId") appleId: String
    ) {
        // GitHub 인증 URL 구성
        val scope = URLEncoder.encode("repo user", "UTF-8")
        val state = URLEncoder.encode(appleId, "UTF-8")  // appleId를 state로 사용

        val githubAuthUrl =
            "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=$scope&state=$state"
        httpServletResponse.sendRedirect(githubAuthUrl)
    }

    @GetMapping("/login/oauth2/code/github")
    fun githubCallback(
        @RequestParam("code") code: String,
        @RequestParam("state") appleId: String,
        httpServletResponse: HttpServletResponse
    ) {
        val tokenResponse = gitHubOAuthService.processGitHubOAuth(code, appleId)
        val redirectUrl = "myapp://callback?token=${tokenResponse.token}"
        httpServletResponse.sendRedirect(redirectUrl)
    }
}