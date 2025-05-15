package io.junseok.todeveloperdo.scheduler.fcm

import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk
import io.mockk.verify

class FcmSchedulerTest : FunSpec({

    val allTodosCompletedStrategy = mockk<AllTodosCompletedStrategy>(relaxed = true)
    val proceedingTodoReminderStrategy = mockk<ProceedingTodoReminderStrategy>(relaxed = true)
    val dailyLogReminderStrategy = mockk<DailyLogReminderStrategy>(relaxed = true)
    val todoNotRegisteredStrategy = mockk<TodoNotRegisteredStrategy>(relaxed = true)
    val fcmDispatcher = mockk<FcmDispatcher>(relaxed = true)

    val scheduler = FcmScheduler(
        allTodosCompletedStrategy,
        proceedingTodoReminderStrategy,
        dailyLogReminderStrategy,
        todoNotRegisteredStrategy,
        fcmDispatcher
    )

    test("20시 스케줄러가 할 일 진행중 알림을 전달해야 한다") {
        scheduler.sendNotificationScheduler()
        verify(exactly = 1) { fcmDispatcher.dispatch(proceedingTodoReminderStrategy) }
    }

    test("8시 스케줄러가 할 일 리마인더 알림을 전달해야 한다") {
        scheduler.sendMorningNotificationScheduler()
        verify(exactly = 1) { fcmDispatcher.dispatch(dailyLogReminderStrategy) }
    }

    test("12시 스케줄러가 할 일 미등록 알림을 전달해야 한다") {
        scheduler.sendAfternoonNotificationScheduler()
        verify(exactly = 1) { fcmDispatcher.dispatch(todoNotRegisteredStrategy) }
    }

    test("23시 스케줄러가 할 일 완료 알림을 전달해야 한다") {
        scheduler.sendEveningNotificationScheduler()
        verify(exactly = 1) { fcmDispatcher.dispatch(allTodosCompletedStrategy) }
    }
})
