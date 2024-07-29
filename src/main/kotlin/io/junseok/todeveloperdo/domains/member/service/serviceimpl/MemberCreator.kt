package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import org.springframework.stereotype.Component

@Component
class MemberCreator() {
    fun generatorMember(gitUserResponse: GitUserResponse,accessToken:String) = Member(
        gitHubUsername = gitUserResponse.username,
        avatarUrl = gitUserResponse.avatarUrl,
        gitHubUrl = gitUserResponse.gitUrl,
        gitHubToken = accessToken,
    )
}