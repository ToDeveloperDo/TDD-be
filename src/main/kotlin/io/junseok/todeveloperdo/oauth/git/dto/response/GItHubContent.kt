package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GItHubContent @JsonCreator constructor(
    @JsonProperty("name") val name: String,
    @JsonProperty("path") val path: String,
    @JsonProperty("sha") val sha: String,
    @JsonProperty("size") val size: Int,
    @JsonProperty("url") val url: String,
    @JsonProperty("html_url") val htmlUrl: String,
    @JsonProperty("git_url") val gitUrl: String,
    @JsonProperty("download_url") val downloadUrl: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("_links") val links: GitHubLink
)