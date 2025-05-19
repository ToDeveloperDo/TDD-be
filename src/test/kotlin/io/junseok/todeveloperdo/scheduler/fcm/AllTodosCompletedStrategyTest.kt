package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.SetUpData
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
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
    val strategy = AllTodosCompletedStrategy(todoReader, stubDate)


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

    test("clientToken이 빈 값일 경우 필터링 되어야 한다") {
        val member1 = createMember(1L, "appleId", "repo", "FcmToken")
        val member2 = createMember(2L, "appleId", "repo", "")
        val member3 = createMember(3L, "appleId", "repo", "  ")
        val todoLists = listOf(
            createMemberTodoList(1L, today.minusWeeks(1), TodoStatus.DONE, member1),
            createMemberTodoList(2L, today.minusWeeks(2), TodoStatus.DONE, member2),
            createMemberTodoList(3L, today.minusWeeks(3), TodoStatus.DONE, member3)
        )

        every {
            todoReader.findTodoListByTodoStatus(
                today,
                TodoStatus.DONE
            )
        } returns todoLists

        val result = strategy.getFcmRequests()

        result.size shouldBe 1
        result.first().clientToken shouldBe "FcmToken"
    }

})

