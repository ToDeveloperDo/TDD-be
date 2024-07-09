package io.junseok.todeveloperdo.presentation.memberfriend.dto.response

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend

data class MemberFriendResponse(
    val friendUsername: String,
    val friendGitUrl: String
)

fun MemberFriend.toMemberFriendResponse() = MemberFriendResponse(
    friendUsername = this.friendUsername,
    friendGitUrl = this.friendGitUrl
)


