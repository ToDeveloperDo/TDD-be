package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import org.springframework.stereotype.Component

@Component
class FcmDispatcher(
    private val fcmProcessor: FcmProcessor
) {
    fun dispatch(strategy: NotificationStrategy){
        val type = strategy.getNotificationType()
        strategy.getFcmRequests().forEach{ request ->
            try {
                fcmProcessor.pushNotification(request, type)
            } catch (e: Exception) {
                println("Failed to send notification to ${request.gitUserName}: ${e.message}")
            }
        }
    }
}