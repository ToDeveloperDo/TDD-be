package io.junseok.todeveloperdo.domains.memberfriend.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

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

    @Query(
        "select case " +
                "when count(mf)>0 then true else false end " +
                "from MemberFriend mf " +
                "where (mf.senderMember = : senderMember and " +
                "mf.receiverMember = : receiverMember and " +
                "mf.friendStatus = : friendStatus)" +
                " or " +
                "(mf.senderMember = : receiverMember and " +
                "mf.receiverMember = : senderMember and " +
                "mf.friendStatus = : friendStatus)"
    )
    fun isFriendShip(
        @Param(value = "senderMember") senderMember: Member,
        @Param(value = "receiverMember") receiverMember: Member,
        @Param(value = "friendStatus") friendStatus: FriendStatus
    ): Boolean
}