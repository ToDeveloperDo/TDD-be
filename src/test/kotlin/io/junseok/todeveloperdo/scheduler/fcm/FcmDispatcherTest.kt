package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.*

class FcmDispatcherTest : FunSpec({
    val fcmProcessor = mockk<FcmProcessor>()
    val strategy = mockk<NotificationStrategy>()
    val fcmDispatcher = FcmDispatcher(fcmProcessor)

    val type = NotificationType.DAILY_LOG_REMINDER
    val request1 = createFcmRequest("clientToken1")
    val request2 = createFcmRequest("clientToken2")
    val request3 = createFcmRequest("clientToken3")

    test("FCM 전략마다 전송이 성공하는 경우") {
        every { strategy.getNotificationType() } returns type
        every { strategy.getFcmRequests() } returns listOf(request1)

        fcmDispatcher.dispatch(strategy)

        verify(exactly = 1) { fcmProcessor.pushNotification(request1,type) }
    }


    test("FCM 전략마다 전송이 실패하는 경우") {
        every { strategy.getNotificationType() } returns type
        every { strategy.getFcmRequests() } returns listOf(request2, request3)

        every { fcmProcessor.pushNotification(request2, type) } throws RuntimeException("FCM 오류")
        every { fcmProcessor.pushNotification(request3, type) } just runs

        fcmDispatcher.dispatch(strategy)
        verify(exactly = 1) { fcmProcessor.pushNotification(request2, type) }
        verify(exactly = 1) { fcmProcessor.pushNotification(request3, type) }

    }
})

fun createFcmRequest(clientToken: String) = FcmRequest(
    clientToken = clientToken,
    gitUserName = "gitUserName"
)
