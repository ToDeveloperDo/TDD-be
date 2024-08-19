package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import org.springframework.stereotype.Component

@Component
class MemberFriendCreator {
    fun create(
        memberFriendId: MemberFriendId,
        member: Member,
        friendMember: Member
    ) = MemberFriend(
        memberFriendId = memberFriendId,
        senderMember = member,
        receiverMember = friendMember,
        friendStatus = FriendStatus.NOT_FRIEND
    )
}