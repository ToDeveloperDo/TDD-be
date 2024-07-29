package io.junseok.todeveloperdo.presentation.member.dto.response

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

data class MemberInfoResponse(
    val username :String,
    val avatarUrl :String,
    val gitUrl :String,
)

fun Member.toMemberInfoResponse() = MemberInfoResponse(
    username = this.gitHubUsername!!,
    avatarUrl = this.avatarUrl!!,
    gitUrl = this.gitHubUrl!!
)