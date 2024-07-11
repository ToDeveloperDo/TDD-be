package io.junseok.todeveloperdo.oauth.git.dto.request

data class GitHubRequest(
    val repoName: String,
    val description: String?="",
    val isPrivate: Boolean
)
