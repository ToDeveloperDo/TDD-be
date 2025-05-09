package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toDailyFcmRequest
import io.junseok.todeveloperdo.util.TimeProvider
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType.NOT_YET_TODO_REGISTERED
import org.springframework.stereotype.Component

@Component
class TodoNotRegisteredStrategy(
    private val memberRepository: MemberRepository,
    private val timeProvider: TimeProvider
) : NotificationStrategy {
    override fun getFcmRequests(): List<FcmRequest> =
        memberRepository.findMemberNotWithDeadLine(timeProvider.now())
            .filter { !it.clientToken.isNullOrBlank() }
            .map { it.toDailyFcmRequest() }

    override fun getNotificationType(): NotificationType = NOT_YET_TODO_REGISTERED
}