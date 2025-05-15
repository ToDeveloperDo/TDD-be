package io.junseok.todeveloperdo.global.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.scheduler.fcm.NotificationFactory.setNotification
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType
import org.springframework.stereotype.Component
import java.util.*

@Component
class FcmProcessor(
    private val firebaseMessaging: FirebaseMessaging
) {
    fun pushNotification(
        fcmRequest: FcmRequest,
        type: NotificationType
    ) = run {
        if (fcmRequest.clientToken.isNotBlank()) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(fcmRequest.setNotification(type))
                .build()

            firebaseMessaging.send(message)
        }
    }
}