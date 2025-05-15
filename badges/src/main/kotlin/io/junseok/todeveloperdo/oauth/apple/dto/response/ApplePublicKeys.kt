package io.junseok.todeveloperdo.oauth.apple.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ApplePublicKeys @JsonCreator constructor(
    @JsonProperty("keys") val keys: List<ApplePublicKey>
)