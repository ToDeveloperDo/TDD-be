package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toDailyFcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.NotificationFactory.toFcmRequest
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
        ).map { it.toFcmRequest()}.distinct()

        fcmRequestList.stream()
            .forEach { request ->
                try {
                    fcmProcessor.dailyNotification(request)
                } catch (e: Exception) {
                    println("Failed to send notification to ${request.gitUserName}: ${e.message}")
                }
            }
    }

    @Transactional
    @Scheduled(cron = "0 0 8 * * *")
    fun sendMorningNotificationScheduler() {
        memberRepository.findAll()
            .stream()
            .map { it.toDailyFcmRequest() }
            .forEach { request ->
                try {
                    fcmProcessor.morningNotification(request)
                } catch (e: Exception) {
                    println("Failed to send notification to ${request.gitUserName}: ${e.message}")
                }
            }
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * *")
    fun sendAfternoonNotificationScheduler() {
        memberRepository.findMemberNotWithDeadLine(LocalDate.now())
            .stream()
            .map { it.toDailyFcmRequest() }
            .forEach { request ->
                try {
                    fcmProcessor.afternoonNotification(request)
                } catch (e: Exception) {
                    println("Failed to send notification to ${request.gitUserName}: ${e.message}")
                }
            }
    }

    @Transactional
    @Scheduled(cron = "0 0 23 * * *")
    fun sendEveningNotificationScheduler() {
        val fcmRequestList = todoListRepository.findAllByDeadlineAndTodoStatus(
            LocalDate.now(),
            TodoStatus.DONE
        ).map { it.toFcmRequest()}.distinct()

        fcmRequestList.stream()
            .forEach { request ->
                try {
                    fcmProcessor.eveningNotification(request)
                } catch (e: Exception) {
                    println("Failed to send notification to ${request.gitUserName}: ${e.message}")
                }
            }
    }
}