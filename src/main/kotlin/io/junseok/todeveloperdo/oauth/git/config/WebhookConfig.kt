package io.junseok.todeveloperdo.oauth.git.config

import com.fasterxml.jackson.annotation.JsonProperty

data class WebhookConfig(
    @JsonProperty("url") val url: String,
    @JsonProperty("content_type") val contentType: String = "json",
    @JsonProperty("secret") val secret: String
)
