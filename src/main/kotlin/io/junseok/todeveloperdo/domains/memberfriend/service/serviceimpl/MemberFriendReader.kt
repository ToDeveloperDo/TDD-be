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
    private val memberFriendRepository: MemberFriendRepository,
) {

    /**
     * NOTE
     * 친구 중에 내가 보낸 친구 목록 조회
     */
    @Transactional(readOnly = true)
    fun findSenderMemberList(member: Member, friendStatus: FriendStatus): List<MemberFriend> =
        memberFriendRepository.findBySenderMemberAndFriendStatus(member, friendStatus)

    /**
     * NOTE
     * 친구 중에 요청을 받았던 친구 목록 조회
     */
    @Transactional(readOnly = true)
    fun findReceiverMemberList(member: Member, friendStatus: FriendStatus): List<MemberFriend> =
        memberFriendRepository.findByReceiverMemberAndFriendStatus(member, friendStatus)

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
     * 나와 친구인 사용자 전부 조회
     */
    @Transactional(readOnly = true)
    fun findAllWithFriend(member: Member) =
        memberFriendRepository.findAllByFriend(member, FriendStatus.FOLLOWING)

    @Transactional(readOnly = true)
    fun findFriend(member: Member, friendMember: Member, friendStatus: FriendStatus) =
        memberFriendRepository.findByFriendRelationship(
            member,
            friendMember,
            friendStatus
        ) ?: throw ToDeveloperDoException { ErrorCode.NOT_FRIENDSHIP }

    @Transactional(readOnly = true)
    fun findAllFriends(member: Member, status: FriendStatus): List<MemberFriend> {
        val sent = memberFriendRepository.findBySenderMemberAndFriendStatus(member, status)
        val received = memberFriendRepository.findByReceiverMemberAndFriendStatus(member, status)
        return (sent + received).distinct()
    }
}