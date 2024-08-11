package io.junseok.todeveloperdo.presentation.member.dto.response

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus

data class MemberResponse(
    val memberId: Long,
    val username :String,
    val avatarUrl :String,
    val gitUrl :String,
    val friendStatus: FriendStatus
)

fun Member.toMemberResponse(friendStatus: FriendStatus) = MemberResponse(
    memberId = this.memberId!!,
    username = this.gitHubUsername!!,
    avatarUrl = this.avatarUrl!!,
    gitUrl = this.gitHubUrl!!,
    friendStatus = friendStatus
)