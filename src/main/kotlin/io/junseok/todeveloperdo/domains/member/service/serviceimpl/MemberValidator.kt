package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberValidator(
    private val memberRepository: MemberRepository,
) {
    @Transactional(readOnly = true)
    fun isExistGitMember(username: String) = memberRepository.existsByGitHubUsername(username)

    fun isExistRepo(member: Member) =
        member.gitHubRepo ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_REPO }

    @Transactional(readOnly = true)
    fun isExistMember(appleId: String) = memberRepository.existsByAppleId(appleId)
}