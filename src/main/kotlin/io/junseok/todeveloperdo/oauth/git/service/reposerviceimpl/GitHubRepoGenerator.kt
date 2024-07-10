package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import org.springframework.stereotype.Component

@Component
class GitHubRepoGenerator {
    fun generatorRepo(gitHubRequest: GitHubRequest): Map<String, Any> {
        return mapOf(
            "name" to gitHubRequest.repoName,
            "description" to gitHubRequest.description!!,
            "private" to gitHubRequest.isPrivate,
            "auto_init" to true
        )
    }
}