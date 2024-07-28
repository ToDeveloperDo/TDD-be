package io.junseok.todeveloperdo.oauth.apple.dto.response

fun Map<String, Any>.toAppleUserResponse(): AppleUserResponse {
    return AppleUserResponse(
        sub = this["sub"].toString(),
        email = this["email"].toString(),
        name = this["name"]?.toString()
    )
}

data class AppleUserResponse(
    val sub: String,
    val email: String,
    val name: String?
)
