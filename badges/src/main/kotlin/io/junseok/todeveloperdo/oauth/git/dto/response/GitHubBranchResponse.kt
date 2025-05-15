package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubBranchResponse @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("commit") val commit: GitHubCommit
)