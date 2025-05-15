package io.junseok.todeveloperdo.global.fcm

import com.fasterxml.jackson.annotation.JsonProperty

data class FcmCredentials(
    @JsonProperty("type") val type: String,
    @JsonProperty("project_id") val project_id: String,
    @JsonProperty("private_key_id") val private_key_id: String,
    @JsonProperty("private_key") val private_key: String,
    @JsonProperty("client_email") val client_email: String,
    @JsonProperty("client_id") val client_id: String,
    @JsonProperty("auth_uri") val auth_uri: String,
    @JsonProperty("token_uri") val token_uri: String,
    @JsonProperty("auth_provider_x509_cert_url") val auth_provider_x509_cert_url: String,
    @JsonProperty("client_x509_cert_url") val client_x509_cert_url: String,
    @JsonProperty("universe_domain") val universe_domain: String
)
