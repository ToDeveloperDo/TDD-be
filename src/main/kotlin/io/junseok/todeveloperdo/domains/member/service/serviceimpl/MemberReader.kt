package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberReader(private val memberRepository: MemberRepository ) {
    @Transactional(readOnly = true)
    fun getMember(username: String): Member = memberRepository.findByUsername(username)
        ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }
}