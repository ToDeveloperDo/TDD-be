package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import org.springframework.stereotype.Component

@Component
class MemberCreator() {
    fun generatorMember(gitUserResponse: GitUserResponse,accessToken:String) = Member(
        username = gitUserResponse.username,
        nickname = gitUserResponse.nickname,
        avatarUrl = gitUserResponse.avatarUrl,
        gitUrl = gitUserResponse.gitUrl,
        gitHubToken = accessToken,
        activated = true,
        authority = Authority.ROLE_USER
    )
}