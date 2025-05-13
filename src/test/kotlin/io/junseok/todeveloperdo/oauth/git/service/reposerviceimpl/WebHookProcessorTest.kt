package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberUpdater
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.oauth.git.dto.response.PayloadResponse
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.DELETE_REPO_ACTION
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.RENAMED_REPO_ACTION
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.REPOSITORY
import io.kotest.core.spec.style.FunSpec
import io.mockk.*

class WebHookProcessorTest : FunSpec({
    val payloadCreator = mockk<PayloadCreator>()
    val memberReader = mockk<MemberReader>()
    val memberUpdater = mockk<MemberUpdater>()
    val webHookProcessor = WebHookProcessor(
        payloadCreator,
        memberReader,
        memberUpdater
    )
    afterTest {
        clearMocks(payloadCreator, memberReader, memberUpdater)
    }


    test("repository renamed 이벤트를 처리해야 한다") {
        val member = createMember(1, "appleId", "repo")
        val payload = mapOf(
            "action" to RENAMED_REPO_ACTION,
            "repository" to mapOf(
                "name" to "new-repo",
                "owner" to mapOf("login" to member.gitHubUsername!!)
            )
        )
        val payloadResponse = createPayloadResponse()

        every { payloadCreator.create(payload) } returns payloadResponse
        every { memberReader.findByGitUserName(member.gitHubUsername!!) } returns member
        every { memberUpdater.updateMemberRepo(payloadResponse.newRepoName, member) } just runs

        webHookProcessor.process(payload, REPOSITORY)
        verify { memberUpdater.updateMemberRepo("new-repo", member) }
    }

    test("repository deleted 이벤트를 처리해야 한다") {
        val member = createMember(1, "appleId", "repo")
        val payload = mapOf(
            "action" to DELETE_REPO_ACTION,
            "repository" to mapOf(
                "name" to "old-repo",
                "owner" to mapOf("login" to member.gitHubUsername!!)
            )
        )

        val response = createPayloadResponse()

        every { payloadCreator.create(payload) } returns response
        every { memberReader.findByGitUserName(member.gitHubUsername!!) } returns member
        every { memberUpdater.removeMemberRepo(member) } just runs

        webHookProcessor.process(payload, REPOSITORY)

        verify { memberUpdater.removeMemberRepo(member) }
    }

    test("repository 외의 이벤트는 RENAMED_REPO_ACTION 작업을 하지 않는다") {
        val member = createMember(1, "appleId", "repo")
        val payload = mapOf(
            "action" to RENAMED_REPO_ACTION,
            "repository" to mapOf(
                "name" to "some-repo",
                "owner" to mapOf("login" to member.gitHubUsername!!)
            )
        )
        val payloadResponse = createPayloadResponse()

        webHookProcessor.process(payload, "push")

        verify(exactly = 0) { payloadCreator.create(payload) }
        verify(exactly = 0) { memberReader.findByGitUserName(member.gitHubUsername!!) }
        verify(exactly = 0) { memberUpdater.updateMemberRepo(payloadResponse.newRepoName, member) }
    }
    test("repositoryㅇ 외의 이벤트는 DELETE_REPO_ACTION 작업을 하지 않는다") {
        val member = createMember(1, "appleId", "repo")
        val payload = mapOf(
            "action" to DELETE_REPO_ACTION,
            "repository" to mapOf(
                "name" to "some-repo",
                "owner" to mapOf("login" to member.gitHubUsername!!)
            )
        )

        webHookProcessor.process(payload, "push")

        verify(exactly = 0) { payloadCreator.create(payload) }
        verify(exactly = 0) { memberReader.findByGitUserName(member.gitHubUsername!!) }
        verify(exactly = 0) { memberUpdater.removeMemberRepo(member) }
    }
})

fun createPayloadResponse() = PayloadResponse(
    newRepoName = "new-repo",
    username = "username"
)