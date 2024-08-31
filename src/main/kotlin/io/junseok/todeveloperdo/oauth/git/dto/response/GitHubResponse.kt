package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubResponse(
    @JsonProperty("id") val id: Long?,
    @JsonProperty("name") val name: String?,
    @JsonProperty("full_name") val fullName: String?,
    @JsonProperty("owner") val owner: Owner
)

data class Owner(
    @JsonProperty("login") val login: String
)
