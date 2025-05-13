package io.junseok.todeveloperdo.oauth.git.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberUpdater
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.toGithubRepo
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.PING
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.REPOSITORY
import io.junseok.todeveloperdo.oauth.git.service.loginserviceimpl.GitLinkValidator
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.RepoValidator
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.WebHookCreator
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.WebHookProcessor
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.createGitHubResponse
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDate

class GitHubServiceTest : FunSpec({
    val gitHubRepoClient = mockk<GitHubRepoClient>()
    val readMeProcessor = mockk<ReadMeProcessor>()
    val repoValidator = mockk<RepoValidator>()
    val memberReader = mockk<MemberReader>()
    val memberUpdater = mockk<MemberUpdater>()
    val gitLinkValidator = mockk<GitLinkValidator>()
    val webHookCreator = mockk<WebHookCreator>()
    val webHookProcessor = mockk<WebHookProcessor>()
    val today = LocalDate.of(2025,5,13)
    val timeProvider = StubDateProvider(today)

    afterTest {
        clearMocks(webHookProcessor)
    }
    val gitHubService = GitHubService(
        gitHubRepoClient = gitHubRepoClient,
        readMeProcessor = readMeProcessor,
        repoValidator = repoValidator,
        memberReader = memberReader,
        memberUpdater = memberUpdater,
        gitLinkValidator = gitLinkValidator,
        webHookCreator = webHookCreator,
        webHookProcessor = webHookProcessor,
        timeProvider = timeProvider
    )

    test("깃허브 레포와 README.md가 정상적으로 생성되어야한다.") {
        val member = createMember(1, "appleId")
        val bearerToken = "Bearer gitToken"
        val gitHubResponse = createGitHubResponse()
        val gitHubRequest = createGitHubRequest()

        every { repoValidator.isValid(gitHubRequest.repoName) } just runs
        every { memberReader.getMember(any()) } returns member
        every { memberUpdater.updateMemberRepo(gitHubRequest.repoName, member) } just runs
        every {
            gitHubRepoClient.createRepository(
                bearerToken,
                gitHubRequest.toGithubRepo()
            )
        } returns gitHubResponse
        every { webHookCreator.create(bearerToken, gitHubResponse) } just runs
        every {
            readMeProcessor.generatorReadMe(
                bearerToken,
                member,
                gitHubRequest.repoName,
                timeProvider.nowDateTime()
            )
        } just runs

        val result =
            gitHubService.createRepository(gitHubRequest, member.gitHubUsername!!)

        result shouldBe gitHubResponse
    }

    test("깃 링크가 존재하면 정상적으로 실행되어야 한다."){
        every { gitLinkValidator.isGitLink("appleId") } just runs

        gitHubService.checkGitLink("appleId")

        verify(exactly = 1) { gitLinkValidator.isGitLink("appleId") }
    }

    test("event가 PING이 아니면 webHookProcessor.process가 호출되어야 한다") {
        val payload = mapOf("key" to "value")
        val event = REPOSITORY

        every { webHookProcessor.process(payload, event) } just runs

        gitHubService.webhookProcess(payload, event)

        verify(exactly = 1) { webHookProcessor.process(payload, event) }
    }

    test("event가 PING이면 webHookProcessor.process가 호출되지 않아야 한다") {
        val payload = mapOf("key" to "value")
        val event = PING

        gitHubService.webhookProcess(payload, event)

        verify(exactly = 0) {
            webHookProcessor.process(any(), any())
        }
    }

})

fun createGitHubRequest() = GitHubRequest(
    repoName = "repoName",
    description = "description",
    isPrivate = true
)