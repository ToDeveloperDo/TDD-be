package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class FileCommitResponse @JsonCreator constructor(
    @JsonProperty("content") val content: GItHubContent,
    @JsonProperty("commit") val commit: GitHubCommit
)
