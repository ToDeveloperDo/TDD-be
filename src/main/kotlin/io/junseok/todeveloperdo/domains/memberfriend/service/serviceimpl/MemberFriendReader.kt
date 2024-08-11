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

    /**
     * NOTE
     * 친구 중에 내가 보낸 친구 목록 조회
     */
    @Transactional(readOnly = true)
    fun findSenderMemberList(member: Member): List<MemberFriend> =
        memberFriendRepository.findBySenderMemberAndFriendStatus(member, FriendStatus.FOLLOWING)

    /**
     * NOTE
     * 친구 중에 요청을 받았던 친구 목록 조회
     */
    @Transactional(readOnly = true)
    fun findReceiverMemberList(member: Member): List<MemberFriend> =
        memberFriendRepository.findByReceiverMemberAndFriendStatus(member, FriendStatus.FOLLOWING)

    /**
     * NOTE
     * 요청 보낸 친구 찾기
     */
    @Transactional(readOnly = true)
    fun findSenderMemberAndReceiverMember(friendMember: Member, member: Member): MemberFriend =
        memberFriendRepository.findBySenderMemberAndReceiverMember(friendMember, member)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_REQUEST_FRIEND }

    /**
     * NOTE
     * 받은 요청 목록 조회(친구X)
     */
    @Transactional(readOnly = true)
    fun receiverMemberByFriendStatus(member: Member): List<MemberFriend> =
        memberFriendRepository.findByReceiverMemberAndFriendStatus(member, FriendStatus.NOT_FRIEND)

    /**
     * NOTE
     * 내가 보낸 요청 목록 조회(친구X)
     */
    @Transactional(readOnly = true)
    fun senderMemberByFriendStatus(member: Member): List<MemberFriend> =
        memberFriendRepository.findBySenderMemberAndFriendStatus(member, FriendStatus.NOT_FRIEND)

    /**
     * NOTE
     * 나와 친구인 사용자 전부 조회
     */
    @Transactional(readOnly = true)
    fun findAllWithFriend(member: Member) =
        memberFriendRepository.findAllByFriend(member,FriendStatus.FOLLOWING)
}