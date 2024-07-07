package io.junseok.todeveloperdo

import io.junseok.todeveloperdo.oauth.git.GitHubOAuthService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin
class LoginController(
    private val gitHubOAuthService: GitHubOAuthService,
) {
    @Value("\${github.client-id}")
    private lateinit var clientId: String

    @Value("\${github.redirect-uri}")
    private lateinit var redirectUri: String

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @GetMapping("/git/login")
    fun redirectToGithub(httpServletResponse: HttpServletResponse) {
        // 설정 값이 제대로 주입되었는지 로그로 출력하여 확인
        logger.info("Client ID: $clientId")
        logger.info("Redirect URI: $redirectUri")

        // GitHub 인증 URL 구성
        val githubAuthUrl = "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri"
        logger.info("success login")
        httpServletResponse.sendRedirect(githubAuthUrl)
    }

    @GetMapping("/login/oauth2/code/github")
    fun githubCallback(@RequestParam("code") code: String): String {
        logger.info("Received GitHub code: $code")
        val userInfo = gitHubOAuthService.processGitHubOAuth(code)
        logger.info("GitHub User Info: $userInfo")
        return userInfo
    }
}