package io.junseok.todeveloperdo.oauth.git.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberUpdater
import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubRequest
import io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl.GitHubRepoGenerator
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.RepoValidator
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.stereotype.Service

@Service
class GitHubService(
    private val gitHubRepoClient: GitHubRepoClient,
    private val gitHubRepoGenerator: GitHubRepoGenerator,
    private val readMeProcessor: ReadMeProcessor,
    private val repoValidator: RepoValidator,
    private val memberReader: MemberReader,
    private val memberUpdater: MemberUpdater
) {
    /**
     * 레포 생성 및 README 생성
     */
    fun createRepository(gitHubRequest: GitHubRequest): Map<String, Any> {
        repoValidator.isValid(gitHubRequest.repoName)
        val member = memberReader.getMember(gitHubRequest.username)
        memberUpdater.updateMemberRepo(gitHubRequest.repoName, member)
        val bearerToken = member.gitHubToken.trim().toGeneratorBearerToken()
        val body = gitHubRepoGenerator.generatorRepo(gitHubRequest)
        val repository = gitHubRepoClient.createRepository(bearerToken, body)
        readMeProcessor.generatorReadMe(bearerToken, member, gitHubRequest.repoName)
        return repository
    }

    companion object {
        const val ISSUE_CLOSED = "closed"
        const val PATH = "README.md"
    }
}
