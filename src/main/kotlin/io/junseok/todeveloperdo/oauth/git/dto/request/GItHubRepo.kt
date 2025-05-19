package io.junseok.todeveloperdo.oauth.git.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class GItHubRepo(
    @JsonProperty("name") val name: String,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("private") val private: Boolean = false,
    @JsonProperty("auto_init") val autoInit: Boolean = true
)