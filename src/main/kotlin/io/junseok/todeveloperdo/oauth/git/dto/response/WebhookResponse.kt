package io.junseok.todeveloperdo.oauth.git.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class WebhookResponse @JsonCreator constructor(
    @JsonProperty("id") val id: Long,
    @JsonProperty("url") val url: String,
    @JsonProperty("test_url") val testUrl: String,
    @JsonProperty("ping_url") val pingUrl: String
)
