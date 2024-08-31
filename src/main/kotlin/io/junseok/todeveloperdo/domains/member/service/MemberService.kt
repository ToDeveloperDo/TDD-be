package io.junseok.todeveloperdo.domains.member.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.toMemberInfoResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberService(
    private val memberReader: MemberReader,
    private val memberUpdater: MemberUpdater,
    private val memberValidator: MemberValidator,
    private val memberDeleter: MemberDeleter,
    private val memberProcessor: MemberProcessor
) {
    fun createGitMember(
        gitUserResponse: GitUserResponse,
        accessToken: String,
        username: String
    ) {
        val member = memberReader.getMember(username)
        if (!memberValidator.isExistGitMember(gitUserResponse.username)) {
            memberUpdater.updateGitMemberInfo(gitUserResponse, accessToken, member)
        } else {
            memberUpdater.updateMemberToken(accessToken, member)
        }

    }

    fun findMember(username: String): MemberInfoResponse {
        val member = memberReader.getMember(username)
        return member.toMemberInfoResponse()
    }

    fun deleteMember(username: String) {
        memberDeleter.removeMember(username)
    }

    fun findAllMember(appleId: String) = memberProcessor.findMemberList(appleId)
    fun updateMember(username: String, newRepoName: String) {
        val member = memberReader.findByGitUserName(username)
        memberUpdater.updateMemberRepo(newRepoName,member)
    }
}