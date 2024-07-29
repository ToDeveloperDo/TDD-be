package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.service.GitHubOAuthService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.security.Principal
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin
class LoginController(
    private val gitHubOAuthService: GitHubOAuthService,
) {
    @Value("\${spring.security.oauth2.client.registration.github.client-id}")
    private lateinit var clientId: String

    @Value("\${spring.security.oauth2.client.registration.github.redirect-uri}")
    private lateinit var redirectUri: String

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @GetMapping("/git/login")
    fun redirectToGithub(httpServletResponse: HttpServletResponse) {
        // 설정 값이 제대로 주입되었는지 로그로 출력하여 확인
        logger.info("Client ID: $clientId")
        logger.info("Redirect URI: $redirectUri")
        // GitHub 인증 URL 구성
        val scope = URLEncoder.encode("repo user", "UTF-8")
        val githubAuthUrl =
            "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=$scope"
        logger.info("success login")
        logger.info("Redirecting to GitHub URL: $githubAuthUrl")
        httpServletResponse.sendRedirect(githubAuthUrl)
    }

    @GetMapping("/login/oauth2/code/github")
    fun githubCallback(
        @RequestParam("code") code: String,
        httpServletResponse: HttpServletResponse,
        principal: Principal
    ) {
        logger.info("Received GitHub code: $code")
        val tokenResponse = gitHubOAuthService.processGitHubOAuth(code,principal.name)
        logger.info("GitHub User Info: $tokenResponse")

        // JWT 토큰을 포함한 리디렉션 URL 생성
        val redirectUrl = "myapp://callback?token=${tokenResponse.token}"
        logger.info(redirectUrl)
        httpServletResponse.sendRedirect(redirectUrl)
    }
}