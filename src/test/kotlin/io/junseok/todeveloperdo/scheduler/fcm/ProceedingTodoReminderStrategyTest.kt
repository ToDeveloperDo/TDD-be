package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.SetUpData
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class ProceedingTodoReminderStrategyTest : FunSpec({
    val todoReader = mockk<TodoReader>()
    val strategy = ProceedingTodoReminderStrategy(todoReader)
    val today = LocalDate.of(2025, 5, 6)

    test("FCM 요청 리스트를 필터링하여 반환해야 한다") {
        val setUpData = SetUpData.listData(today)
        every {
            todoReader.findTodoListByTodoStatus(
                today,
                TodoStatus.PROCEED
            )
        } returns setUpData.todoLists

        val result = strategy.getFcmRequests()

        result.first().clientToken shouldBe "Fcm"
    }
    test("알림 타입이 DAILY_TODO_REMINDER이어야 한다") {
        strategy.getNotificationType() shouldBe NotificationType.DAILY_TODO_REMINDER
    }

})
