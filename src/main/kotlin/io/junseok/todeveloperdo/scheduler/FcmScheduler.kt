package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.global.fcm.FcmService
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.global.fcm.dto.request.toFcmRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class FcmScheduler(
    private val todoListRepository: TodoListRepository,
    private val fcmService: FcmService,
) {
    @Transactional
    @Scheduled(cron = "0 0 22 * * *")
    fun sendNotificationScheduler() {
        val fcmRequestList = todoListRepository.findAllByDeadlineAndTodoStatus(
            LocalDate.now(),
            TodoStatus.PROCEED
        ).map { it.toFcmRequest() }.distinct()

        fcmRequestList.stream()
            .forEach { request ->
                fcmService.sendNotification(request)
            }
    }
}