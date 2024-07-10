package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubLink @JsonCreator constructor(
    @JsonProperty("self") val self: String,
    @JsonProperty("git") val git: String,
    @JsonProperty("html") val html: String
)