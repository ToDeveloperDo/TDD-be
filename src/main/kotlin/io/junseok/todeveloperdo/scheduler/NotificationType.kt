package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest

enum class NotificationType(
    val messageProvider: (FcmRequest) -> String,
) {
    DAILY_TODO_REMINDER({ "${it.gitUserName}님! 아직 오늘의 할 일이 남았어요." }),
    DAILY_LOG_REMINDER({ "오늘 하루를 기록해 성장을 향해 나아가세요!" }),
    NOT_YET_TODO_REGISTERED({ "아직 오늘 할 일이 등록되지 않았어요!" }),
    ALL_TODOS_COMPLETED({ "할 일을 다 마무리 하셨네요👍\n오늘 하루도 고생하셨습니다!" }),
    FRIEND_REQUEST({ "${it.gitUserName}님이 친구요청을 보냈습니다!" }),
    FRIEND_REQUEST_ACCEPTED({ "${it.gitUserName}님이 친구요청을 수락했습니다!" })
}