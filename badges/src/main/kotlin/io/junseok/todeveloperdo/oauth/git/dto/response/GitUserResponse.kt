package io.junseok.todeveloperdo.oauth.git.dto.response

data class GitUserResponse(
    val username: String,
    val avatarUrl: String,
    val gitUrl: String
)
fun Map<String, Any>.toGitUserResponse() = GitUserResponse(
    username = this["login"].toString(),
    avatarUrl = this["avatar_url"].toString(),
    gitUrl = this["html_url"].toString()
)