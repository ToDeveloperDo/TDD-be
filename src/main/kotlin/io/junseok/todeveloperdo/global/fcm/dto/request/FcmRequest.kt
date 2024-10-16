package io.junseok.todeveloperdo.global.fcm.dto.request

import com.google.firebase.messaging.Notification
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList

fun FcmRequest.toNotification() =
    Notification.builder()
        .setTitle("TDD")
        .setBody("${this.gitUserName}님! 아직 오늘의 할 일이 남았어요.")
        .build()

fun FcmRequest.toReceiveNotification() =
    Notification.builder()
        .setTitle("TDD")
        .setBody("${this.gitUserName}님이 친구요청을 보냈습니다!")
        .build()

fun FcmRequest.toSendNotification() =
    Notification.builder()
        .setTitle("TDD")
        .setBody("${this.gitUserName}님아 친구요청을 수락했습니다!")
        .build()


fun MemberTodoList.toFcmRequest() = FcmRequest(
    clientToken = this.member.clientToken!!,
    gitUserName = this.member.gitHubUsername!!
)

data class FcmRequest(
    val clientToken: String,
    val gitUserName: String
)