package io.junseok.todeveloperdo.global.fcm.dto.request

import com.google.firebase.messaging.Notification
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList

object NotificationFactory {
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
            .setBody("${this.gitUserName}님이 친구요청을 수락했습니다!")
            .build()

    fun toSendMorningNotification() =
        Notification.builder()
            .setTitle("TDD")
            .setBody("오늘 하루를 기록해 성장을 향해 나아가세요!")
            .build()

    fun toSendAfternoonNotification() =
        Notification.builder()
            .setTitle("TDD")
            .setBody("아직 오늘 할 일이 등록되지 않았어요!")
            .build()

    fun toSendEveningNotification() =
        Notification.builder()
            .setTitle("TDD")
            .setBody("할 일을 다 마무리 하셨네요👍\n오늘 하루도 고생하셨습니다!")
            .build()
    fun MemberTodoList.toFcmRequest() = FcmRequest(
        clientToken = this.member.clientToken!!,
        gitUserName = this.member.gitHubUsername!!
    )

    fun Member.toDailyFcmRequest() = FcmRequest(
        clientToken = this.clientToken!!,
        gitUserName = this.gitHubUsername!!
    )
}

data class FcmRequest(
    val clientToken: String,
    val gitUserName: String? = null,
)