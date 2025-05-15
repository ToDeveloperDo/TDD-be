package io.junseok.todeveloperdo.global.fcm.dto.request

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList

data class FcmRequest(
    val clientToken: String,
    val gitUserName: String? = null,
)

fun MemberTodoList.toFcmRequest() = FcmRequest(
    clientToken = this.member.clientToken!!,
    gitUserName = this.member.gitHubUsername!!
)

fun Member.toDailyFcmRequest() = FcmRequest(
    clientToken = this.clientToken!!,
    gitUserName = this.gitHubUsername!!
)
