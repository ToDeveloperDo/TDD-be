package io.junseok.todeveloperdo.presentation.memberfriend.dto.response

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend

data class MemberFriendResponse(
    val memberId: Long,
    val friendUsername: String,
    val friendGitUrl: String
)

fun toMemberFriendResponse(member: Member) = MemberFriendResponse(
    memberId = member.memberId!!,
    friendUsername = member.username,
    friendGitUrl = member.gitUrl
)