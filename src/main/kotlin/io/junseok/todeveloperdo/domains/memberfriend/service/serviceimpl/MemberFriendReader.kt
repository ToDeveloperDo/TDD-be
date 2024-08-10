package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFriendReader(
    private val memberFriendRepository: MemberFriendRepository
) {

    @Transactional(readOnly = true)
    fun findSenderMemberList(member: Member): List<MemberFriend> =
        memberFriendRepository.findBySenderMemberAndFriendStatus(member, FriendStatus.FOLLOW)

    @Transactional(readOnly = true)
    fun findReceiverMemberList(member: Member): List<MemberFriend> =
        memberFriendRepository.findByReceiverMemberAndFriendStatus(member, FriendStatus.FOLLOW)

    @Transactional(readOnly = true)
    fun findSenderMemberAndReceiverMember(member: Member, friendMember: Member): MemberFriend =
        memberFriendRepository.findBySenderMemberAndReceiverMember(member, friendMember)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_REQUEST_FRIEND }

    @Transactional(readOnly = true)
    fun receiverMemberByFriendStatus(member: Member): List<MemberFriend> =
        memberFriendRepository.findByReceiverMemberAndFriendStatus(member, FriendStatus.UNFOLLOW)

    @Transactional(readOnly = true)
    fun senderMemberByFriendStatus(member: Member): List<MemberFriend> =
        memberFriendRepository.findBySenderMemberAndFriendStatus(member, FriendStatus.UNFOLLOW)
}