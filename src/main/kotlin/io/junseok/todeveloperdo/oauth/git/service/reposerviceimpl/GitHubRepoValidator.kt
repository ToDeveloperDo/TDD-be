package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.client.GitHubRepoClient
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.stereotype.Component

@Component
class GitHubRepoValidator(
    private val memberReader: MemberReader,
    private val memberValidator: MemberValidator,
    private val gitHubRepoClient: GitHubRepoClient
) {
    fun isExistRepo(appleId: String) {
        val member = memberReader.getMember(appleId)
        memberValidator.isExistRepo(member)

        try {
            gitHubRepoClient.isExistRepo(
                member.gitHubUsername!!,
                member.gitHubRepo!!,
                member.gitHubToken!!.toGeneratorBearerToken()
            ).statusCode.is2xxSuccessful
        } catch (e: Exception) {
            throw ToDeveloperDoException { ErrorCode.NOT_EXIST_REPO }
        }
    }
}