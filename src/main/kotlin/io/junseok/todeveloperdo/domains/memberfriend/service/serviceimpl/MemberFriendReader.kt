package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFriendReader(private val memberFriendRepository: MemberFriendRepository) {
    @Transactional(readOnly = true)
    fun getMemberFriendList(member: Member): MutableList<MemberFriendResponse> =
        memberFriendRepository.findByMember(member)
            .stream()
            .map { it.toMemberFriendResponse() }
            .toList()

    @Transactional(readOnly = true)
    fun getMemberFriend(memberId: Long) =
        memberFriendRepository.findByIdOrNull(memberId)?.toMemberFriendResponse()
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }
}