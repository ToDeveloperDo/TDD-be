package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class TodoNotRegisteredStrategyTest : FunSpec({
    val memberRepository = mockk<MemberRepository>()
    val today = LocalDate.of(2025, 5, 6)
    val stubDate = StubDateProvider(today)
    val todoNotRegisteredStrategy = TodoNotRegisteredStrategy(memberRepository,stubDate)

    test("FCM 요청 리스트를 필터링하여 반환해야 한다") {
        val validMember = createTestMember(1, "validToken")
        val inValidMember = createTestMember(2)
        every { memberRepository.findMemberNotWithDeadLine(today) } returns listOf(
            validMember,
            inValidMember
        )

        val result = todoNotRegisteredStrategy.getFcmRequests()

        result.size shouldBe 1
        result.first().clientToken shouldBe "validToken"
    }

    test("clientToken이 null이거나 blank인 멤버는 제외되어야 한다") {
        val validMember = createTestMember(1, "validToken")
        val blankMember = createTestMember(2, "")
        val nullTokenMember = createTestMember(3, null)

        every { memberRepository.findMemberNotWithDeadLine(today) } returns listOf(validMember, blankMember, nullTokenMember)

        val result = todoNotRegisteredStrategy.getFcmRequests()

        result.size shouldBe 1
        result.first().clientToken shouldBe "validToken"
    }


    test("알림 타입이 NOT_YET_TODO_REGISTERED여야 한다") {
        todoNotRegisteredStrategy.getNotificationType() shouldBe NotificationType.NOT_YET_TODO_REGISTERED
    }
})
