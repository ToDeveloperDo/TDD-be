package io.junseok.todeveloperdo.oauth

import io.junseok.todeveloperdo.oauth.dto.response.GitUserResponse


fun Map<String, Any>.toGitUserResponse() = GitUserResponse(
    username = this["login"].toString(),
    nickname = this["name"].toString(),
    avatarUrl = this["avatar_url"].toString(),
    gitUrl = this["html_url"].toString()
)