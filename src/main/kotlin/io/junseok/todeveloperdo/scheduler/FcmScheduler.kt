package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toDailyFcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toFcmRequest
import io.junseok.todeveloperdo.scheduler.NotificationType.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class FcmScheduler(
    private val todoListRepository: TodoListRepository,
    private val fcmProcessor: FcmProcessor,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    @Scheduled(cron = "0 0 20 * * *")
    fun sendNotificationScheduler() {
        val fcmRequestList = todoListRepository.findAllByDeadlineAndTodoStatus(
            LocalDate.now(),
            TodoStatus.PROCEED
        ).map { it.toFcmRequest() }.distinct()
            .filter { it.clientToken.isNotBlank() }
        dispatchNotificationsByType(fcmRequestList, DAILY_TODO_REMINDER)
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * *")
    fun sendMorningNotificationScheduler() {
        val fcmRequests = memberRepository.findAll()
            .filter { !it.clientToken.isNullOrBlank() }
            .map { it.toDailyFcmRequest() }
        dispatchNotificationsByType(fcmRequests, DAILY_LOG_REMINDER)
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * *")
    fun sendAfternoonNotificationScheduler() {
        val fcmRequests = memberRepository.findMemberNotWithDeadLine(LocalDate.now())
            .filter { !it.clientToken.isNullOrBlank() }
            .map { it.toDailyFcmRequest() }
        dispatchNotificationsByType(fcmRequests, NOT_YET_TODO_REGISTERED)
    }

    @Transactional
    @Scheduled(cron = "0 0 23 * * *")
    fun sendEveningNotificationScheduler() {
        val fcmRequests = todoListRepository.findAllByDeadlineAndTodoStatus(
            LocalDate.now(),
            TodoStatus.DONE
        ).map { it.toFcmRequest() }.distinct()
            .filter { it.clientToken.isNotBlank() }
        dispatchNotificationsByType(fcmRequests, ALL_TODOS_COMPLETED)
    }

    fun dispatchNotificationsByType(fcmRequests: List<FcmRequest>, type: NotificationType) = run {
        fcmRequests.forEach { request ->
            try {
                fcmProcessor.pushNotification(request, type)
            } catch (e: Exception) {
                println("Failed to send notification to ${request.gitUserName}: ${e.message}")
            }
        }
    }
}