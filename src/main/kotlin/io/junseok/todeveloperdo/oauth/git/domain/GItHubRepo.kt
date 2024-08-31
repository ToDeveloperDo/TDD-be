package io.junseok.todeveloperdo.oauth.git.domain

data class GItHubRepo(
    val repoName: String,
    val description: String? = "",
    val isPrivate: Boolean,
    val auto_init: Boolean? = true,
)