package io.junseok.todeveloperdo.event.issue

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.issue.dto.request.IssueCloseEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.event.issue.dto.request.IssueUpdateEventRequest
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.createGitHubIssueStateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class IssueEventProcessorTest : FunSpec({
    val eventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)
    val issueEventProcessor = IssueEventProcessor(eventPublisher)

    beforeTest {
        clearAllMocks()
    }

    test("create()를 호출하면 IssueEventRequest가 생성된다.") {
        val member = createMember(1, "appleId")
        val todoRequest = createTodoRequest()
        val issueFuture = CompletableFuture<Int>().apply { complete(123) }

        every { eventPublisher.publishEvent(any()) } just runs

        val result = issueEventProcessor.create(member, todoRequest)
        val expected = createIssueEventRequest(member, todoRequest, issueFuture)

        result.issueNumber.complete(123)
        result.member shouldBe expected.member
        result.todoRequest shouldBe expected.todoRequest
        result.issueNumber.get() shouldBe expected.issueNumber.get()
    }

    test("close()를 호출하면 이슈가 close상태로 변경되어야한다.") {
        val member = createMember(1, "appleId")
        val issueNumber = 123
        val state = "state"
        val issueCloseEventRequest = createIssueCloseEventRequest(member, issueNumber)
        every { eventPublisher.publishEvent(issueCloseEventRequest) } just runs

        issueEventProcessor.close(member, issueNumber, state)

        verify(exactly = 1) { eventPublisher.publishEvent(issueCloseEventRequest) }
    }

    test("update()가 호출되면 이슈의 내용이 수정된다.") {
        val member = createMember(1, "appleId")
        val issueNumber = 123
        val todoCreate = createTodoCreate(1, member)
        val issueUpdateEventRequest =
            createIssueUpdateEventRequest(member, issueNumber, todoCreate)

        every { eventPublisher.publishEvent(issueUpdateEventRequest) } just runs

        issueEventProcessor.update(member, issueNumber, todoCreate)

        verify(exactly = 1) { eventPublisher.publishEvent(issueUpdateEventRequest) }
    }

})

fun createIssueEventRequest(
    member: Member,
    todoRequest: TodoRequest,
    issueNumber: CompletableFuture<Int> = CompletableFuture()
) = IssueEventRequest(
    member = member,
    todoRequest = todoRequest,
    issueNumber = issueNumber
)

fun createIssueCloseEventRequest(
    member: Member,
    issueNumber: Int,
) = IssueCloseEventRequest(
    member = member,
    issueNumber = issueNumber,
    gitHubIssueStateRequest = createGitHubIssueStateRequest()
)

fun createIssueUpdateEventRequest(
    member: Member,
    issueNumber: Int,
    todoCreate: TodoCreate
) = IssueUpdateEventRequest(
    member = member,
    issueNumber = issueNumber,
    todoCreate = todoCreate
)

fun createTodoCreate(issueId: Long, member: Member): TodoCreate {
    val today = LocalDate.of(2025, 5, 13)
    return TodoCreate(
        issueId = issueId,
        content = "content",
        memo = "memo",
        tag = "tag",
        deadline = today,
        member = member
    )
}