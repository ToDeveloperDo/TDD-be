package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/github")
@CrossOrigin
class GitHubController(
    private val gitHubService: GitHubService,
) {
    @PostMapping("/create/repo")
    fun registerGitRepo(
        @RequestBody gitHubRequest: GitHubRequest,
        principal: Principal,
    ): GitHubResponse = gitHubService.createRepository(gitHubRequest, principal.name)

    @GetMapping("/check")
    fun isGitHubLink(principal: Principal) = gitHubService.checkGitLink(principal.name)

    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody payload: Map<String, Any>,
        @RequestHeader("X-GitHub-Event") event: String,
    ) {
        gitHubService.webhookProcess(payload,event)
    }
}