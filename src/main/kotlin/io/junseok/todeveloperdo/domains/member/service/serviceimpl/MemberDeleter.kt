package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberDeleter(private val memberRepository: MemberRepository) {
    @Transactional
    fun removeMember(username: String) = memberRepository.deleteByUsername(username)
}