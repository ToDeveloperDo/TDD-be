package io.junseok.todeveloperdo.global.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toNotification
import org.springframework.stereotype.Service
import java.util.Objects

/*
@Service
class FcmService(private val fcmProcessor: FcmProcessor) {
    fun dailyNotification(fcmRequest: FcmRequest) {
        if (Objects.nonNull(fcmRequest.clientToken)) {
            val message = Message.builder()
                .setToken(fcmRequest.clientToken)
                .setNotification(fcmRequest.toNotification())
                .build()

            FirebaseMessaging.getInstance().send(message)
        }
    }

    fun receiveNotification(fcmRequest: FcmRequest) =
        fcmProcessor.byReceiveNotification(fcmRequest)

    fun sendNotification(fcmRequest: FcmRequest) =
        fcmProcessor.bySendNotification(fcmRequest)
}*/
