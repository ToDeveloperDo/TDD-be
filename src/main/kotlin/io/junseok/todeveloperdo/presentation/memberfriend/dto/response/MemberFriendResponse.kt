package io.junseok.todeveloperdo.presentation.memberfriend.dto.response

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

data class MemberFriendResponse(
    val memberId: Long,
    val friendUsername: String,
    val friendGitUrl: String
)

fun toMemberFriendResponse(member: Member) = MemberFriendResponse(
    memberId = member.memberId!!,
    friendUsername = member.gitHubUsername!!,
    friendGitUrl = member.gitHubUrl!!
)