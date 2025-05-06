package io.junseok.todeveloperdo.scheduler.fcm

import com.google.firebase.messaging.Notification
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest

object NotificationFactory {
    fun FcmRequest.setNotification(type: NotificationType) = Notification.builder()
        .setTitle("TDD")
        .setBody(type.messageProvider(this))
        .build()

}