package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.SetUpData
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class AllTodosCompletedStrategyTest : FunSpec({
    val todoReader = mockk<TodoReader>()
    val today = LocalDate.of(2025, 5, 6)
    val stubDate = StubDateProvider(today)
    val strategy = AllTodosCompletedStrategy(todoReader,stubDate)


    test("FCM 요청 리스트를 필터링하여 반환해야 한다") {
        val setUpData = SetUpData.listData(today)
        every {
            todoReader.findTodoListByTodoStatus(
                today,
                TodoStatus.DONE
            )
        } returns setUpData.todoLists

        val result = strategy.getFcmRequests()

        result.first().clientToken shouldBe "Fcm"
    }
    test("알림 타입이 ALL_TODOS_COMPLETED이어야 한다") {
        strategy.getNotificationType() shouldBe NotificationType.ALL_TODOS_COMPLETED
    }
})
