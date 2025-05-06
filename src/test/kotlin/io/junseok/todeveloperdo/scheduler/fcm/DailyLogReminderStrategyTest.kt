package io.junseok.todeveloperdo.scheduler.fcm

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class DailyLogReminderStrategyTest : FunSpec({
    val memberReader = mockk<MemberReader>()
    val dailyLogReminderStrategy = DailyLogReminderStrategy(memberReader)

    test("FCM 요청 리스트를 필터링하여 반환해야 한다") {
        val validMember = createTestMember(1, "validToken")
        val inValidMember = createTestMember(2)
        every { memberReader.getAllMember() } returns listOf(validMember,inValidMember)

        val result = dailyLogReminderStrategy.getFcmRequests()

        result.size shouldBe 1
        result.first().clientToken shouldBe "validToken"
    }

    test("알림 타입이 DAILY_LOG_REMINDER여야 한다") {
        dailyLogReminderStrategy.getNotificationType() shouldBe NotificationType.DAILY_LOG_REMINDER
    }
})
fun createTestMember(id: Long,clientToken: String ="") = Member(
    memberId = id,
    appleId = "appleId",
    appleRefreshToken = "appleRefreshToken",
    appleEmail = "appleEmail",
    gitHubUsername = "username",
    gitHubToken = "gitToken",
    gitHubRepo = "repo",
    avatarUrl = "avatar",
    gitHubUrl = "gitUrl",
    clientToken = clientToken,
    authority = Authority.ROLE_USER
)