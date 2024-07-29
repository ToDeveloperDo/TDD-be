package io.junseok.todeveloperdo.oauth.apple.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class ApplePublicKey @JsonCreator constructor(
    @JsonProperty("kty") val kty: String,
    @JsonProperty("kid") val kid: String,
    @JsonProperty("use") val use: String,
    @JsonProperty("alg") val alg: String,
    @JsonProperty("n") val n: String,
    @JsonProperty("e") val e: String
)
