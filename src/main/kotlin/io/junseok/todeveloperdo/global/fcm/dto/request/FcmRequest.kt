package io.junseok.todeveloperdo.global.fcm.dto.request

import com.google.firebase.messaging.Notification
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.scheduler.NotificationType

object NotificationFactory {
    fun FcmRequest.setNotification(type: NotificationType) = Notification.builder()
        .setTitle("TDD")
        .setBody(type.messageProvider(this))
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