package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubCommit @JsonCreator constructor(
    @JsonProperty("sha") val sha: String
)