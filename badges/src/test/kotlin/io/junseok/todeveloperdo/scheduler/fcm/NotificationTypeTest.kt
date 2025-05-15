package io.junseok.todeveloperdo.scheduler.fcm

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NotificationTypeTest : FunSpec({

    val request = createFcmRequest("clientToken")

    test("DAILY_TODO_REMINDER 메시지 생성") {
        NotificationType.DAILY_TODO_REMINDER.messageProvider(request) shouldBe
                "${request.gitUserName}님! 아직 오늘의 할 일이 남았어요."
    }

    test("DAILY_LOG_REMINDER 메시지 생성") {
        NotificationType.DAILY_LOG_REMINDER.messageProvider(request) shouldBe
                "오늘 하루를 기록해 성장을 향해 나아가세요!"
    }

    test("NOT_YET_TODO_REGISTERED 메시지 생성") {
        NotificationType.NOT_YET_TODO_REGISTERED.messageProvider(request) shouldBe
                "아직 오늘 할 일이 등록되지 않았어요!"
    }

    test("ALL_TODOS_COMPLETED 메시지 생성") {
        NotificationType.ALL_TODOS_COMPLETED.messageProvider(request) shouldBe
                "할 일을 다 마무리 하셨네요👍\n오늘 하루도 고생하셨습니다!"
    }

    test("FRIEND_REQUEST 메시지 생성") {
        NotificationType.FRIEND_REQUEST.messageProvider(request) shouldBe
                "${request.gitUserName}님이 친구요청을 보냈습니다!"
    }

    test("FRIEND_REQUEST_ACCEPTED 메시지 생성") {
        NotificationType.FRIEND_REQUEST_ACCEPTED.messageProvider(request) shouldBe
                "${request.gitUserName}님이 친구요청을 수락했습니다!"
    }
})

