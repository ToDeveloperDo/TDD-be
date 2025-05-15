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

    fun findBySenderMemberAndReceiverMember(friendMember: Member, member: Member): MemberFriend?

    @Query(
        "select mf " +
                "from MemberFriend mf " +
                "where mf.friendStatus = :friendStatus and " +
                "mf.receiverMember = :member or mf.senderMember = :member "
    )
    fun findAllByFriend(
        @Param(value = "member") member: Member,
        @Param(value = "friendStatus") friendStatus: FriendStatus
    ): List<MemberFriend>

    @Query(
        "select case " +
                "when count(mf)>0 then true else false end " +
                "from MemberFriend mf " +
                "where (mf.senderMember = :senderMember and " +
                "mf.receiverMember = :receiverMember and " +
                "mf.friendStatus = :friendStatus)"
    )
    fun isSendFriend(
        @Param(value = "senderMember") senderMember: Member,
        @Param(value = "receiverMember") receiverMember: Member,
        @Param(value = "friendStatus") friendStatus: FriendStatus
    ): Boolean

    @Query(
        "select case " +
                "when count(mf)>0 then true else false end " +
                "from MemberFriend mf " +
                "where (mf.senderMember = :receiverMember and " +
                "mf.receiverMember = :senderMember and " +
                "mf.friendStatus = :friendStatus)"
    )
    fun isRequestFriend(
        @Param(value = "senderMember") senderMember: Member,
        @Param(value = "receiverMember") receiverMember: Member,
        @Param(value = "friendStatus") friendStatus: FriendStatus
    ): Boolean

    @Query(
        "select mf " +
                "from MemberFriend mf " +
                "where mf.friendStatus = :friendStatus and " +
                "mf.senderMember = :senderMember and mf.receiverMember = :receiverMember " +
                "or mf.senderMember = :receiverMember and mf.receiverMember = :senderMember"
    )
    fun findByFriendRelationship(
        @Param(value = "senderMember") senderMember: Member,
        @Param(value = "receiverMember") receiverMember: Member,
        @Param(value = "friendStatus") friendStatus: FriendStatus
    ): MemberFriend?
}