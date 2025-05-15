package io.junseok.todeveloperdo.event

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.junseok.todeveloperdo.event.issue.IssueEventProcessor
import io.junseok.todeveloperdo.event.issue.createIssueEventRequest
import io.junseok.todeveloperdo.event.issue.createTodoCreate
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class EventProcessorTest : FunSpec({
    val issueEventProcessor = mockk<IssueEventProcessor>()
    val readMeEventProcessor = mockk<ReadMeEventProcessor>()
    val eventProcessor = EventProcessor(issueEventProcessor, readMeEventProcessor)

    beforeTest {
        clearAllMocks()
    }

    test("createIssue() 호출 시 issueEventProcessor의 create()가 호출되고 결과를 반환한다") {
        val member = createMember(1, "appleId", "repo")
        val todoRequest = createTodoRequest()
        val issueFuture = CompletableFuture<Int>().apply { complete(123) }
        val issueEventRequest = createIssueEventRequest(member, todoRequest, issueFuture)

        every { issueEventProcessor.create(member, todoRequest) } returns issueEventRequest

        val result = eventProcessor.createIssue(member, todoRequest)

        result shouldBe issueEventRequest
    }

    test("issueNumber가 존재하면 이슈를 닫고 README 생성을 요청한다") {
        val member = createMember(1, "appleId", "repo")
        val today = LocalDate.of(2025, 5, 13)
        val state = "state"
        val todoList = createMemberTodoList(
            1,
            today,
            TodoStatus.PROCEED,
            member,
            1
        )

        every { issueEventProcessor.close(member, todoList.issueNumber!!, state) } just runs
        every { readMeEventProcessor.create(member) } just runs

        eventProcessor.closeIssueWithReadMe(member, todoList, state)

        verifyOrder {
            issueEventProcessor.close(member, 1, state)
            readMeEventProcessor.create(member)
        }
    }

    test("issueNumber가 null이면 아무 작업도 하지 않는다") {
        val member = createMember(1, "appleId", "repo")
        val today = LocalDate.of(2025, 5, 13)
        val state = "state"
        val todoList = createMemberTodoList(
            1,
            today,
            TodoStatus.PROCEED,
            member
        )

        eventProcessor.closeIssueWithReadMe(member, todoList, state)

        verify { issueEventProcessor wasNot Called }
        verify { readMeEventProcessor wasNot Called }
    }

    test("updateIssueWithReadMe() 호출 시 issueEventProcessor의 update()와 readMeEventProcessor의 create()가 호출된다.") {
        val member = createMember(1, "appleId", "repo")
        val issueNumber = 1
        val todoCreate = createTodoCreate(1, member)

        every { issueEventProcessor.update(member, issueNumber, todoCreate) } just runs
        every { readMeEventProcessor.create(member) } just runs

        eventProcessor.updateIssueWithReadMe(member, issueNumber, todoCreate)

        verify { issueEventProcessor.update(member, issueNumber, todoCreate) }
        verify { readMeEventProcessor.create(member) }
    }
})
