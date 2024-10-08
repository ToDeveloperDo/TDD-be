package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberReader(private val memberRepository: MemberRepository) {
    @Transactional(readOnly = true)
    fun getMember(username: String): Member = memberRepository.findByAppleId(username)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }

    @Transactional(readOnly = true)
    fun getAllMember(): List<Member> = memberRepository.findAll()

    @Transactional(readOnly = true)
    fun getFriendMember(memberId: Long) = memberRepository.findByIdOrNull(memberId)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }

    @Transactional(readOnly = true)
    fun getMembersExcludeMe(member: Member): List<Member> = memberRepository.findAll()
        .filter { it.memberId != member.memberId }

    @Transactional(readOnly = true)
    fun findByGitUserName(username: String) = memberRepository.findByGitHubUsername(username)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }
}