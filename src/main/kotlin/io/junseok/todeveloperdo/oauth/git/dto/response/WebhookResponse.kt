package io.junseok.todeveloperdo.oauth.git.dto.response

data class WebhookResponse(
    val id: Long,
    val url: String,
    val test_url: String,
    val ping_url: String
)
