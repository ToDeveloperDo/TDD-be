package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberUpdater {
    @Transactional
    fun updateMemberToken(accessToken: String, member: Member) =
        member.updateGitHubToken(accessToken)

    @Transactional
    fun updateMemberRepo(repoName: String, member: Member) = member.updateGitHubRepo(repoName)

    @Transactional
    fun removeMemberRepo(member: Member) = member.removeRepo()
    @Transactional
    fun updateGitMemberInfo(
        gitUserResponse: GitUserResponse,
        accessToken: String,
        member: Member
    ) {
        member.updateGitInfo(gitUserResponse,accessToken)
    }

    @Transactional
    fun updateFcmToken(fcmToken: String, member: Member) {
        member.updateClientToken(fcmToken)
    }
}