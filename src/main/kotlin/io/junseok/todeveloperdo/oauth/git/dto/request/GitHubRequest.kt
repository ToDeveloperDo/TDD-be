package io.junseok.todeveloperdo.oauth.git.dto.request

import io.junseok.todeveloperdo.oauth.git.domain.GItHubRepo

fun GitHubRequest.toGithubRepo() =
    GItHubRepo(
        repoName = this.repoName,
        description = this.description!!,
        isPrivate = this.isPrivate,
        auto_init = true
    )


data class GitHubRequest(
    val repoName: String,
    val description: String?="",
    val isPrivate: Boolean
)
