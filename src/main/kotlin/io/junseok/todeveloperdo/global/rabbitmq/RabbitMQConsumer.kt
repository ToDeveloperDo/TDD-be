package io.junseok.todeveloperdo.global.rabbitmq

import io.junseok.todeveloperdo.scheduler.fcm.FcmScheduler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class RabbitMQConsumer(
    private val fcmScheduler: FcmScheduler
) {

    @RabbitListener(queues = ["todo-queue"])
    fun receiveMessage(token: String) {
        try {
            println("📩 Received FCM request: $token")

            fcmScheduler.sendMorningNotificationScheduler()

        } catch (e: Exception) {
            println("❌ Error sending FCM: ${e.message}, sending to DLX")

            throw RuntimeException("FCM 전송 실패, 재시도 필요", e)
        }
    }
}
