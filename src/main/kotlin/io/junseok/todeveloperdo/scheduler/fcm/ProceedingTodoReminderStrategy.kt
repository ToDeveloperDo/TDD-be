package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toFcmRequest
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType.DAILY_TODO_REMINDER
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProceedingTodoReminderStrategy(
    private val todoReader: TodoReader

) : NotificationStrategy {
    override fun getFcmRequests(): List<FcmRequest> =
        todoReader.findTodoListByTodoStatus(
            LocalDate.now(),
            TodoStatus.PROCEED
        ).map { it.toFcmRequest() }.distinct()
            .filter { it.clientToken.isNotBlank() }

    override fun getNotificationType(): NotificationType = DAILY_TODO_REMINDER

}