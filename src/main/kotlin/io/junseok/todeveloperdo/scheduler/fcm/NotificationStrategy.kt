package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest

interface NotificationStrategy {
    fun getFcmRequests(): List<FcmRequest>
    fun getNotificationType(): NotificationType
}
