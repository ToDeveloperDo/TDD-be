package io.junseok.todeveloperdo.oauth.git.dto.request

fun GitHubRequest.toGithubRepo() =
    GItHubRepo(
        name = this.repoName,
        description = this.description!!,
        private = this.isPrivate,
        autoInit = true
    )


data class GitHubRequest(
    val repoName: String,
    val description: String?="",
    val isPrivate: Boolean
)
