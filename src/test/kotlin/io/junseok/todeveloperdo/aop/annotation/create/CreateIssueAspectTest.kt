package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.event.issue.createIssueEventRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.aspectj.lang.ProceedingJoinPoint
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class CreateIssueAspectTest : FunSpec({
    val eventProcessor = mockk<EventProcessor>()
    val memberReader = mockk<MemberReader>()
    val today = LocalDate.of(2025, 5, 13)
    val timeProvider = StubDateProvider(today)
    val createIssueAspect = CreateIssueAspect(eventProcessor, memberReader, timeProvider)

    test("오늘 마감인 할 일이 있을 경우 이슈 생성 후 첫 번째 이슈를 인자로 넘긴다") {
        val matchingRequest = TodoRequest(
            content = "content",
            memo = "memo",
            tag = "tag",
            deadline = today
        )
        val member = createMember(1, "appleId", "repo")
        val todoRequest = createTodoRequest()
        val issueFuture = CompletableFuture<Int>().apply { complete(1) }
        val issueEventRequest = createIssueEventRequest(member, todoRequest, issueFuture)
        val todoRequests = listOf(matchingRequest)
        val username = "username"

        every { memberReader.getMember(username) } returns member
        every { eventProcessor.createIssue(member, todoRequests[0]) } returns issueEventRequest
        val joinPoint = mockk<ProceedingJoinPoint>()
        every { joinPoint.args } returns arrayOf(todoRequests, username, null)

        every { joinPoint.proceed(any()) } answers {
            val newArgs = firstArg<Array<Any?>>()
            newArgs[2] shouldBe issueEventRequest
            "result"
        }

        val result = createIssueAspect.aroundCreateIssue(joinPoint)
        result shouldBe "result"
    }

    test("이슈 생성 후 issueEventRequests가 비어있다면 null을 인자로 반환한다.") {
        val nonMatchingRequest = TodoRequest(
            content = "content",
            memo = "memo",
            tag = "tag",
            deadline = today.plusDays(1)
        )
        val member = createMember(1, "appleId", "repo")
        val todoRequests = listOf(nonMatchingRequest)
        val username = "username"

        every { memberReader.getMember(username) } returns member
        val joinPoint = mockk<ProceedingJoinPoint>()
        every { joinPoint.args } returns arrayOf(todoRequests, username, null)

        every { joinPoint.proceed(any()) } answers {
            val newArgs = firstArg<Array<Any?>>()
            newArgs[2] shouldBe null
            "result"
        }

        val result = createIssueAspect.aroundCreateIssue(joinPoint)
        result shouldBe "result"
    }
})
