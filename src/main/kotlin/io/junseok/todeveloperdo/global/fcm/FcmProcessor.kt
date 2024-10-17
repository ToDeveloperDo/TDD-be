package io.junseok.todeveloperdo.global.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.toReceiveNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.toSendNotification
import org.springframework.stereotype.Component
import java.util.*

@Component
class FcmProcessor {
    fun dailyNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(fcmRequest.toNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }

    fun bySendNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(fcmRequest.toSendNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }

    fun byReceiveNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(fcmRequest.toReceiveNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }
}