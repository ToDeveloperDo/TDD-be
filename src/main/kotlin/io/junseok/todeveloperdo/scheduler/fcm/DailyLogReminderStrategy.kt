package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toDailyFcmRequest
import org.springframework.stereotype.Component

@Component
class DailyLogReminderStrategy(
    private val memberReader: MemberReader,
) : NotificationStrategy {
    override fun getFcmRequests(): List<FcmRequest> =
        memberReader.getAllMember()
            .filter { !it.clientToken.isNullOrBlank() }
            .map { it.toDailyFcmRequest() }

    override fun getNotificationType(): NotificationType = NotificationType.DAILY_LOG_REMINDER

}