package io.junseok.todeveloperdo.presentation.member.dto.response

data class MemberResponse(
    val username :String,
    val avatarUrl :String,
    val gitUrl :String,
    val friendStatus: String
)
