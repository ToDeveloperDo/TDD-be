package io.junseok.todeveloperdo.oauth.apple.dto.response

data class AppleTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Long,
    val refresh_token: String,
    val id_token: String
)