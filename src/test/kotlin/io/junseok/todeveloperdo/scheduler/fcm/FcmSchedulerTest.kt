package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.SetUpData
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class FcmSchedulerTest : FunSpec({
    val fcmProcessor = mockk<FcmProcessor>()
    val memberRepository = mockk<MemberRepository>()
    val memberReader = mockk<MemberReader>()
    val todoReader = mockk<TodoReader>()
    val fcmScheduler = FcmScheduler(
        fcmProcessor,
        memberRepository,
        memberReader,
        todoReader
    )
    val today = LocalDate.of(2025, 5, 6)
    test("오늘 날짜의 진행 중인 할 일 목록에서 FCM 요청 목록이 생성된다") {
        val setUpData = SetUpData.listData(today)
        every {
            todoReader.findTodoListByTodoStatus(
                today,
                TodoStatus.PROCEED
            )
        } returns setUpData.todoLists


    }
})
