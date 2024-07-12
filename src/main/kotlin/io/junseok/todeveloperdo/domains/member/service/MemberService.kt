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
    private val memberSaver: MemberSaver,
    private val memberCreator: MemberCreator,
    private val memberUpdater: MemberUpdater,
    private val memberValidator: MemberValidator,
    private val memberDeleter: MemberDeleter
) {
    fun createMember(gitUserResponse: GitUserResponse, accessToken: String) {
        if (!memberValidator.isExistMember(gitUserResponse.username)) {
            val member = memberCreator.generatorMember(gitUserResponse,accessToken)
            memberSaver.saveMember(member)
        }else{
            val member = memberReader.getMember(gitUserResponse.username)
            memberUpdater.updateMemberToken(accessToken,member)
        }
    }

    fun findMember(username: String): MemberInfoResponse {
        val member = memberReader.getMember(username)
        return member.toMemberInfoResponse()
    }

    fun deleteMember(username: String) {
        memberDeleter.removeMember(username)
    }
}