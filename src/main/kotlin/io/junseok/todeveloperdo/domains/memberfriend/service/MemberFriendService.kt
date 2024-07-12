package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendValidator
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberFriendService(
    private val memberFriendRepository: MemberFriendRepository,
    private val memberReader: MemberReader,
    private val memberFriendValidator: MemberFriendValidator
) {
    fun findMemberFriendList(username: String): List<MemberFriendResponse>? {
        val member = memberReader.getMember(username)
        val senderMember =
            memberFriendRepository.findBySenderMemberAndFriendStatus(member, FriendStatus.FOLLOW)
        val receiverMember =
            memberFriendRepository.findByReceiverMemberAndFriendStatus(member, FriendStatus.FOLLOW)

        val friends = (senderMember + receiverMember).distinct()

        return friends.map { friend ->
            MemberFriendResponse(
                memberId = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.memberId!! //
                } else {
                    friend.senderMember.memberId!!
                },
                friendUsername = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.username
                } else {
                    friend.senderMember.username
                },
                friendGitUrl = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.gitUrl
                } else {
                    friend.senderMember.gitUrl
                }
            )
        }
    }

    fun findMemberFriend(username: String, memberId: Long): MemberFriendResponse {
        val member = memberReader.getFriendMember(memberId)
        return MemberFriendResponse(
            memberId = memberId,
            friendUsername = member.username,
            friendGitUrl = member.gitUrl
        )
    }

    @Transactional
    fun registerFriend(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        val memberFriendId = MemberFriendId(member.memberId!!, friendId)

        val memberFriend = MemberFriend(
            memberFriendId = memberFriendId,
            senderMember = member,
            receiverMember = friendMember,
            friendStatus = FriendStatus.UNFOLLOW
        )
        memberFriendRepository.save(memberFriend)
    }

    @Transactional
    fun deleteFriend(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        val memberFriend =
            memberFriendRepository.findBySenderMemberAndReceiverMember(member, friendMember)
                ?: throw ToDeveloperDoException { ErrorCode.NOT_REQUEST_FRIEND }
        memberFriendRepository.delete(memberFriend)
    }

    @Transactional(readOnly = true)
    fun findWaitFriends(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendRepository.findByReceiverMemberAndFriendStatus(
            member,
            FriendStatus.UNFOLLOW
        )
            .map {toMemberFriendResponse(it.senderMember) }
    }

    @Transactional
    fun approveRequest(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        val memberFriend =
            memberFriendRepository.findBySenderMemberAndReceiverMember(friendMember, member)
                ?: throw ToDeveloperDoException { ErrorCode.NOT_REQUEST_FRIEND }
        memberFriend.updateFriendStatus()
    }

    @Transactional(readOnly = true)
    fun findSendRequestList(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendRepository.findBySenderMemberAndFriendStatus(
            member,
            FriendStatus.UNFOLLOW
        )
            .map { toMemberFriendResponse(it.receiverMember) }
    }
}