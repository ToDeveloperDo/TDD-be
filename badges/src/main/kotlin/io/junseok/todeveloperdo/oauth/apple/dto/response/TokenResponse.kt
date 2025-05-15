package io.junseok.todeveloperdo.oauth.apple.dto.response

data class TokenResponse(
    val idToken: String,
    val refreshToken: String
)

