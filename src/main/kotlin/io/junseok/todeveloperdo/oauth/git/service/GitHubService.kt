package io.junseok.todeveloperdo.oauth.git.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberUpdater
import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.config.WebhookConfig
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.WebhookRequest
import io.junseok.todeveloperdo.oauth.git.dto.request.toGithubRepo
import io.junseok.todeveloperdo.oauth.git.dto.response.GitHubResponse
import io.junseok.todeveloperdo.oauth.git.service.loginserviceimpl.GitLinkValidator
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.RepoValidator
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.GitHubRepoGenerator
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.GitHubRepoValidator
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.WebHookCreator
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.stereotype.Service

@Service
class GitHubService(
    private val gitHubRepoClient: GitHubRepoClient,
    private val gitHubRepoGenerator: GitHubRepoGenerator,
    private val readMeProcessor: ReadMeProcessor,
    private val repoValidator: RepoValidator,
    private val memberReader: MemberReader,
    private val memberUpdater: MemberUpdater,
    private val gitLinkValidator: GitLinkValidator,
    private val gitHubRepoValidator: GitHubRepoValidator,
    private val webHookCreator: WebHookCreator,
) {
    /**
     * 레포 생성 및 README 생성
     */
    fun createRepository(gitHubRequest: GitHubRequest, username: String): GitHubResponse {
        repoValidator.isValid(gitHubRequest.repoName)
        val member = memberReader.getMember(username)
        memberUpdater.updateMemberRepo(gitHubRequest.repoName, member)
        val bearerToken = member.gitHubToken!!.trim().toGeneratorBearerToken()
        //val body = gitHubRepoGenerator.generatorRepo(gitHubRequest)
        println("sdsdsdsds")
        val repository =
            gitHubRepoClient.createRepository(bearerToken, gitHubRequest.toGithubRepo())
        println("repository.name = ${repository.name}")
        webHookCreator.create(bearerToken, repository)
        readMeProcessor.generatorReadMe(bearerToken, member, gitHubRequest.repoName)
        return repository
    }

    fun checkGitLink(appleId: String) = gitLinkValidator.isGitLink(appleId)
    fun checkGitRepo(appleId: String) = gitHubRepoValidator.isExistRepo(appleId)

    companion object {
        const val ISSUE_CLOSED = "closed"
        const val ISSUE_OPEN = "open"
        const val PATH = "README.md"
    }
}
