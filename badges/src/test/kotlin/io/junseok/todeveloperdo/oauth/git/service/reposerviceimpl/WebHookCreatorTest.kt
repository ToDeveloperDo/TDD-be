package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.config.WebhookConfig
import io.junseok.todeveloperdo.oauth.git.dto.request.WebhookRequest
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import io.junseok.todeveloperdo.oauth.git.dto.response.Owner
import io.junseok.todeveloperdo.oauth.git.dto.response.WebhookResponse
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class WebHookCreatorTest : FunSpec({
    val gitHubRepoClient = mockk<GitHubRepoClient>()
    val webHookCreator = WebHookCreator(gitHubRepoClient)

    test("WebHook은 정상적으로 생성되어야한다.") {
        val bearerToken = "token"
        val gitHubResponse = createGitHubResponse()
        val webHookResponse = createWebHookResponse()
        every {
            gitHubRepoClient.createWebhook(
                bearerToken,
                gitHubResponse.owner.login,
                gitHubResponse.name!!,
                any()
            )
        } returns webHookResponse

        webHookCreator.create(bearerToken, gitHubResponse)

        verify(exactly = 1) {
            gitHubRepoClient.createWebhook(
                bearerToken,
                gitHubResponse.owner.login,
                gitHubResponse.name!!,
                any()
            )
        }
    }
})

fun createWebHookResponse() = WebhookResponse(
    id = 1,
    url = "url",
    testUrl = "test_url",
    pingUrl = "pingUrl"
)

fun createWebHookRequest() = WebhookRequest(
    config = createWebHookConfig(),
    events = listOf("repository"),
    active = true
)

fun createWebHookConfig() = WebhookConfig(
    url = "https://api.todeveloperdo.shop/api/github/webhook",
    contentType = "json",
    secret = "tdd-webhook-by-user-0240710"
)

fun createOwner() = Owner(login = "sample-user")

fun createGitHubResponse() = GitHubResponse(
    id = 1L,
    name = "my-repo",
    fullName = "sample-user/my-repo",
    owner = createOwner()
)
