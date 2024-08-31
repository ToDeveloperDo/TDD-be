package io.junseok.todeveloperdo.oauth.git.presentation

import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
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
    private val memberService: MemberService
) {
    @PostMapping("/create/repo")
    fun registerGitRepo(
        @RequestBody gitHubRequest: GitHubRequest,
        principal: Principal
    ):GitHubResponse = gitHubService.createRepository(gitHubRequest,principal.name)

    @GetMapping("/check")
    fun isGitHubLink(principal: Principal) = gitHubService.checkGitLink(principal.name)

    @GetMapping("/check/repo")
    fun isGitHubRepo(principal: Principal) = gitHubService.checkGitRepo(principal.name)

    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody payload: Map<String, Any>,
        @RequestHeader("X-GitHub-Event") event: String
    ) {
        if (event == "repository" && payload["action"] == "renamed") {
            val repository = payload["repository"] as? Map<String, Any>
            val oldRepoName = ((payload["changes"] as Map<String, Map<String, String>>)["repository"]?.get("from"))
            val newRepoName = repository?.get("name") as? String
            val ownerName = (repository?.get("owner") as? Map<String, Any>)?.get("login") as? String

            if (oldRepoName != null && newRepoName != null && ownerName != null) {
                memberService.updateMember(ownerName,newRepoName)
            }
        }
    }
}