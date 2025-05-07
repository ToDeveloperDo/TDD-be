package io.junseok.todeveloperdo.scheduler.fcm

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FcmScheduler(
    private val allTodosCompletedStrategy: AllTodosCompletedStrategy,
    private val proceedingTodoReminderStrategy: ProceedingTodoReminderStrategy,
    private val dailyLogReminderStrategy: DailyLogReminderStrategy,
    private val todoNotRegisteredStrategy: TodoNotRegisteredStrategy,
    private val fcmDispatcher: FcmDispatcher
) {
    @Scheduled(cron = "0 0 20 * * *")
    fun sendNotificationScheduler() = fcmDispatcher.dispatch(proceedingTodoReminderStrategy)

    @Scheduled(cron = "0 0 8 * * *")
    fun sendMorningNotificationScheduler() = fcmDispatcher.dispatch(dailyLogReminderStrategy)

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 12 * * *")
    fun sendAfternoonNotificationScheduler() = fcmDispatcher.dispatch(todoNotRegisteredStrategy)

    @Scheduled(cron = "0 0 23 * * *")
    fun sendEveningNotificationScheduler() = fcmDispatcher.dispatch(allTodosCompletedStrategy)

}