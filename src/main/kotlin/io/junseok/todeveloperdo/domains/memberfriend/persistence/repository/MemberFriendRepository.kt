package io.junseok.todeveloperdo.domains.memberfriend.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import org.springframework.data.jpa.repository.JpaRepository

interface MemberFriendRepository : JpaRepository<MemberFriend, MemberFriendId> {
    fun findBySenderMemberAndFriendStatus(
        senderMember: Member,
        friendStatus: FriendStatus
    ): List<MemberFriend>

    fun findByReceiverMemberAndFriendStatus(
        receiverMember: Member,
        friendStatus: FriendStatus
    ): List<MemberFriend>

    fun findBySenderMemberAndReceiverMember(member: Member, friendMember: Member): MemberFriend?
}