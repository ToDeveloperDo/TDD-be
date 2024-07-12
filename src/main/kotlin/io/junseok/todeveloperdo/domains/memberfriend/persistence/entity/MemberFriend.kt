package io.junseok.todeveloperdo.domains.memberfriend.persistence.entity

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import javax.persistence.*

@Entity
@Table(name = "member_friend")
class MemberFriend(
    @EmbeddedId
    val memberFriendId: MemberFriendId,

    @Enumerated(EnumType.STRING)
    var friendStatus: FriendStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_member_id", insertable = false, updatable = false)
    var senderMember: Member, //요청을 보내는사람(송신자)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_member_id", insertable = false, updatable = false)
    var receiverMember: Member // 요청을 받은 사람(수신자)
) {
    fun updateFriendStatus() {
        this.friendStatus = FriendStatus.FOLLOW
    }

}