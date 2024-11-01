package io.junseok.todeveloperdo.global.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.junseok.todeveloperdo.global.fcm.dto.request.*
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toReceiveNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toSendAfternoonNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toSendEveningNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toSendMorningNotification
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toSendNotification
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

    fun morningNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(toSendMorningNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }

    fun afternoonNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(toSendAfternoonNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }

    fun eveningNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(toSendEveningNotification())
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