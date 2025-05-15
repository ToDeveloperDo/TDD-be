package io.junseok.todeveloperdo.domains.memberfriend.persistence.entity

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class MemberFriendId(
    @Column(name = "sender_member_id")
    val senderMemberId: Long,

    @Column(name = "receiver_member_id")
    val receiverMemberId: Long
): Serializable
