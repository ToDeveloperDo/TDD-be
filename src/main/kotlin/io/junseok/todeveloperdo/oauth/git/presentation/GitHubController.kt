package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/github")
@CrossOrigin
class GitHubController(
    private val gitHubService: GitHubService
) {
    @PostMapping("/create/repo")
    fun registerGitRepo(
        @RequestBody gitHubRequest: GitHubRequest,
        principal: Principal
    ):Map<String,Any> = gitHubService.createRepository(gitHubRequest,principal.name)

    @GetMapping("/check")
    fun isGitHubLink(principal: Principal) = gitHubService.checkGitLink(principal.name)

    @GetMapping("/check/repo")
    fun isGitHubRepo(principal: Principal) = gitHubService.checkGitRepo(principal.name)
}