package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.service.GitHubService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/github")
@CrossOrigin
class GitHubController(
    private val gitHubService: GitHubService
) {
    @PostMapping("/create/repo")
    fun registerGitRepo(
        @RequestBody gitHubRequest: GitHubRequest
    ):Map<String,Any> = gitHubService.createRepository(gitHubRequest)
}