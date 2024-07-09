package io.junseok.todeveloperdo.domains.member.service

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.toMemberInfoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberRepository: MemberRepository
) {
    @Transactional
    fun createMember(gitUserResponse: GitUserResponse, accessToken: String) {
        if (!memberRepository.existsByUsername(gitUserResponse.username)) {
            val member = Member(
                username = gitUserResponse.username,
                nickname = gitUserResponse.nickname,
                avatarUrl = gitUserResponse.avatarUrl,
                gitUrl = gitUserResponse.gitUrl,
                gitHubToken = accessToken,
                activated = true,
                authority = Authority.ROLE_USER
            )
            memberRepository.save(member)
        }else{
            val member = getMember(gitUserResponse.username)
            member.updateGitHubToken(accessToken)
        }
    }

    @Transactional(readOnly = true)
    fun findMember(username: String): MemberInfoResponse {
        val member = getMember(username)
        return member.toMemberInfoResponse()
    }

    @Transactional
    fun registerRepo(repoName:String, username: String){
        val member = getMember(username)
        member.updateGitHubRepo(repoName)
    }

    fun getMember(username: String): Member = memberRepository.findByUsername(username)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }
}