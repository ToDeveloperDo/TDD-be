package io.junseok.todeveloperdo.oauth.git

import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.oauth.git.client.GitHubUserClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/github")
@CrossOrigin
class GitHubController(
    private val gitHubApiClients: GitHubUserClient,
    private val memberService: MemberService
) {
    @PostMapping("/create/repo")
    fun registerGitRepo(
        @RequestBody gitHubRequest: GitHubRequest
    ):Map<String,Any> {
        val member = memberService.getMember(gitHubRequest.username)
        val accessToken = "Bearer ${member.gitHubToken.trim()}"
        val body = mapOf<String,Any>(
            "name" to gitHubRequest.repoName,
            "description" to gitHubRequest.description!!,
            "private" to gitHubRequest.isPrivate,
            "auto_init" to true
        )
        memberService.registerRepo(gitHubRequest.repoName,gitHubRequest.username)
        return gitHubApiClients.createRepository(accessToken,body)
    }
}