package io.junseok.todeveloperdo.presentation.memberfriend.dto.response

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend

data class MemberFriendResponse(
    val memberId: Long,
    val friendUsername: String,
    val friendGitUrl: String
)

fun MemberFriend.toMemberFriendResponse(memberId: Long) = MemberFriendResponse(
    memberId = memberId,
    friendUsername = this.receiverMember.username,
    friendGitUrl = this.receiverMember.gitUrl
)


