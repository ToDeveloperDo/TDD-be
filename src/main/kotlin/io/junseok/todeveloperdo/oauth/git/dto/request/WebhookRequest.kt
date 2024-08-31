package io.junseok.todeveloperdo.oauth.git.dto.request

import io.junseok.todeveloperdo.oauth.git.config.WebhookConfig

data class WebhookRequest(
    val config: WebhookConfig,
    val events: List<String> = listOf("repository"),
    val active: Boolean = true
)