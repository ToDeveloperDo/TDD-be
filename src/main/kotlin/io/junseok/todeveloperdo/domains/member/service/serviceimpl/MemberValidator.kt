package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberValidator(private val memberRepository: MemberRepository) {
    @Transactional(readOnly = true)
    fun isExistMember(username:String) = memberRepository.existsByUsername(username)
}