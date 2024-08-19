package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendReader
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.toMemberResponse
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberProcessor(
    private val memberReader: MemberReader,
    private val memberFriendReader: MemberFriendReader
) {
    @Transactional(readOnly = true)
    fun findMemberList(appleId: String): List<MemberResponse> {
        val member = memberReader.getMember(appleId)
        //내가 받은 요청 목록
        val receiveList = memberFriendReader.receiverMemberByFriendStatus(member)
            .map { it.senderMember.memberId }

        //내가 보낸 친구 목록
        val sendList = memberFriendReader.senderMemberByFriendStatus(member)
            .map { it.receiverMember.memberId }

        //친구인 사람
        val friendList = memberFriendReader.findAllWithFriend(member)

        return memberReader.getMembersExcludeMe(member).map { friend ->
            val friendStatus = when {
                receiveList.contains(friend.memberId) -> FriendStatus.RECEIVE
                sendList.contains(friend.memberId) -> FriendStatus.REQUEST
                friendList.any {
                    it.senderMember == friend || it.receiverMember == friend
                } -> FriendStatus.FOLLOWING

                else -> FriendStatus.NOT_FRIEND
            }
            friend.toMemberResponse(friendStatus)
        }
    }
}