package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toFcmRequest
import io.junseok.todeveloperdo.util.TimeProvider
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType.DAILY_TODO_REMINDER
import org.springframework.stereotype.Component

@Component
class ProceedingTodoReminderStrategy(
    private val todoReader: TodoReader,
    private val timeProvider: TimeProvider

) : NotificationStrategy {
    override fun getFcmRequests(): List<FcmRequest> =
        todoReader.findTodoListByTodoStatus(
            timeProvider.now(),
            TodoStatus.PROCEED
        ).map { it.toFcmRequest() }.distinct()
            .filter { it.clientToken.isNotBlank() }

    override fun getNotificationType(): NotificationType = DAILY_TODO_REMINDER

}