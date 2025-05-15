package io.junseok.todeveloperdo.global.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import io.junseok.todeveloperdo.scheduler.fcm.NotificationFactory.setNotification
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType
import io.junseok.todeveloperdo.scheduler.fcm.createFcmRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FcmProcessorTest : FunSpec({
    val firebaseMessaging = mockk<FirebaseMessaging>()
    val fcmProcessor = FcmProcessor(firebaseMessaging)

    beforeTest {
        clearAllMocks()
    }

    test("사용자의 clientToken이 null 아니면 알림 전송이 되어야한다.") {
        val fcmRequest = createFcmRequest("clientToken")
        val type = NotificationType.DAILY_LOG_REMINDER

        every { firebaseMessaging.send(any()) } returns "message"
        fcmProcessor.pushNotification(fcmRequest, type)

        verify(exactly = 1) { firebaseMessaging.send(any()) }
    }

    test("사용자의 clientToken이 null이면 FCM알림 전송이 실패한다.") {
        val fcmRequest = createFcmRequest("")
        val type = NotificationType.DAILY_LOG_REMINDER

        fcmProcessor.pushNotification(fcmRequest, type)

        verify(exactly = 0) { firebaseMessaging.send(any()) }
    }

})