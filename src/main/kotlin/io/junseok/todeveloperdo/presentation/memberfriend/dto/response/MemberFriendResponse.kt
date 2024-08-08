package io.junseok.todeveloperdo.presentation.memberfriend.dto.response

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

fun Member.toMemberFriendResponse()= MemberFriendResponse(
    memberId = this.memberId!!,
    friendUsername = this.gitHubUsername!!,
    friendGitUrl = this.gitHubUrl!!
)
data class MemberFriendResponse(
    val memberId: Long,
    val friendUsername: String,
    val friendGitUrl: String
)