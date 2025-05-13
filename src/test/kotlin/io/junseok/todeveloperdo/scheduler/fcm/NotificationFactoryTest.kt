package io.junseok.todeveloperdo.scheduler.fcm

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class NotificationFactoryTest : FunSpec({
    test("setNotification()이 정상적으로 실행되어야 한다") {
        val request = createFcmRequest("token")
        val notification = with(NotificationFactory) {
            request.setNotification(NotificationType.DAILY_LOG_REMINDER)
        }
        notification shouldNotBe null

    }
})