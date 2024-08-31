package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import org.springframework.stereotype.Component

@Component
class GitHubRepoGenerator {
    fun generatorRepo(gitHubRequest: GitHubRequest): GitHubRequest {
        return GitHubRequest(
            repoName = gitHubRequest.repoName,
            description = gitHubRequest.description ?: "",
            isPrivate = gitHubRequest.isPrivate
        )
    }
}