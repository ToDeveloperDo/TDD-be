package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberValidator(
    private val memberRepository: MemberRepository,
    private val memberReader: MemberReader
) {
    @Transactional(readOnly = true)
    fun isExistMember(username: String) = memberRepository.existsByUsername(username)

    @Transactional(readOnly = true)
    fun isExistRepo(member: Member) = member.gitHubRepo!!.isNotBlank()
}