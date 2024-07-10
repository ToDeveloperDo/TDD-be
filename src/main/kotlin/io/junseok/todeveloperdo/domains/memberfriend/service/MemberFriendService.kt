package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendReader
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import org.springframework.stereotype.Service

@Service
class MemberFriendService(
    private val memberReader: MemberReader,
    private val memberFriendReader: MemberFriendReader
) {
    fun findMemberFriendList(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.getMemberFriendList(member)
    }

    fun findMemberFriend(username: String, memberId: Long) =
        memberFriendReader.getMemberFriend(memberId)
}