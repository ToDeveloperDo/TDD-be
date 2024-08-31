package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.config.WebhookConfig
import io.junseok.todeveloperdo.oauth.git.dto.request.WebhookRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import org.springframework.stereotype.Component

@Component
class WebHookCreator(
    private val gitHubRepoClient: GitHubRepoClient,
) {
    fun create(bearerToken: String, gitHubResponse: GitHubResponse) {
        val webhookRequest = WebhookRequest(
            config = WebhookConfig(
                url = "https://api.todeveloperdo.shop/api/github/webhook",
                secret = "tdd-webhook-by-user-0240710"
            )
        )
        println("Sdsshdhsjufvjbdsfghjasdfhjeahjfhjdshj")
        val webhookResponse = gitHubRepoClient.createWebhook(
            bearerToken,
            owner = gitHubResponse.owner.login,
            repo = gitHubResponse.name!!,
            webhookRequest = webhookRequest
        )
    }
}