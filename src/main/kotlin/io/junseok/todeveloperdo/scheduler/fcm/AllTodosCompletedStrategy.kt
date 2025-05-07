package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.global.fcm.dto.request.toFcmRequest
import io.junseok.todeveloperdo.scheduler.TimeProvider
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType.ALL_TODOS_COMPLETED
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AllTodosCompletedStrategy(
    private val todoReader: TodoReader,
    private val timeProvider: TimeProvider
) : NotificationStrategy{
    override fun getFcmRequests(): List<FcmRequest> =
        todoReader.findTodoListByTodoStatus(
            timeProvider.now(),
            TodoStatus.DONE
        ).map { it.toFcmRequest() }.distinct()
            .filter { it.clientToken.isNotBlank() }

    override fun getNotificationType(): NotificationType = ALL_TODOS_COMPLETED
}