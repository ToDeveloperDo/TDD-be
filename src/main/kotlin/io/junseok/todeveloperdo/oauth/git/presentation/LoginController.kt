package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.service.GitHubOAuthService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.security.Principal
import javax.servlet.http.HttpServletRequest
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

    @GetMapping("/git/login")
    fun redirectToGithub(
        httpServletResponse: HttpServletResponse,
        //httpServletRequest: HttpServletRequest,
        //principal: Principal
    ) {
        //httpServletRequest.session.setAttribute("principal",principal)
        // GitHub 인증 URL 구성
        val scope = URLEncoder.encode("repo user", "UTF-8")
        val githubAuthUrl =
            "https://github.com/login/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri&scope=$scope"
        httpServletResponse.sendRedirect(githubAuthUrl)
    }

    @GetMapping("/login/oauth2/code/github")
    fun githubCallback(
        @RequestParam("code") code: String,
        @RequestParam("appleId") appleId: String,
        httpServletResponse: HttpServletResponse,
        //httpServletRequest: HttpServletRequest,
    ) {
   /*     val principal = httpServletRequest.session.getAttribute("principal") as? Principal
            ?: throw IllegalStateException("No principal found in session")*/

        val tokenResponse = gitHubOAuthService.processGitHubOAuth(code, appleId)
        val redirectUrl = "myapp://callback?token=${tokenResponse.token}"
        httpServletResponse.sendRedirect(redirectUrl)
    }
}