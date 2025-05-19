package io.junseok.todeveloperdo.aop.annotation.update

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueReader
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.createGitIssue
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoUpdater
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.event.issue.IssueEventProcessor
import io.junseok.todeveloperdo.event.issue.createIssueEventRequest
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.exception.ErrorCode.FAILED_TO_GENERATE_ISSUE
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_CLOSED
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_OPEN
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.util.StubDateProvider
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.aspectj.lang.JoinPoint
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class UpdateAspectTest : FunSpec({
    val eventProcessor = mockk<EventProcessor>(relaxed = true)
    val memberReader = mockk<MemberReader>()
    val todoReader = mockk<TodoReader>()
    val gitIssueReader = mockk<GitIssueReader>()
    val todoUpdater = mockk<TodoUpdater>(relaxed = true)
    val issueEventProcessor = mockk<IssueEventProcessor>(relaxed = true)
    val readMeEventProcessor = mockk<ReadMeEventProcessor>(relaxed = true)
    val today = LocalDate.of(2025, 5, 13)
    val timeProvider = StubDateProvider(today)

    val updateAspect = UpdateAspect(
        eventProcessor,
        memberReader,
        todoReader,
        gitIssueReader,
        todoUpdater,
        issueEventProcessor,
        readMeEventProcessor,
        timeProvider
    )

    beforeTest { clearAllMocks() }

    test("오늘이 마감일이고 issueNumber가 null이면 이슈를 새로 생성하고 업데이트한다") {
        val todoListId = 1L
        val username = "testUser"
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member)
        val gitIssue = createGitIssue(1, today, todoList)
        val issueFuture = CompletableFuture<Int>().apply { complete(1) }
        val issueEventRequest =
            createIssueEventRequest(member, todoRequest, issueFuture)
        val issueNumber = 1

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every { eventProcessor.createIssue(member, todoRequest) } returns issueEventRequest
        every { todoUpdater.modifyIssueNumber(issueNumber, todoList) } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        updateAspect.update(joinPoint)

        verify {
            eventProcessor.createIssue(member, todoRequest)
            todoUpdater.modifyIssueNumber(issueNumber, todoList)
            readMeEventProcessor.create(member)
        }
    }

    test("이슈를 새로 생성됐는데 이슈 번호가 없다면 FAILED_TO_GENERATE_ISSUE 에러를 반환해야한다.") {
        val todoListId = 1L
        val username = "testUser"
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member)
        val gitIssue = createGitIssue(1, today, todoList)
        val issueFuture = CompletableFuture<Int>().apply { complete(null) }
        val issueEventRequest =
            createIssueEventRequest(member, todoRequest, issueFuture)
        val issueNumber = 1

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every { eventProcessor.createIssue(member, todoRequest) } returns issueEventRequest
        every { todoUpdater.modifyIssueNumber(issueNumber, todoList) } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        throwsWith<ToDeveloperDoException>(
            {
                updateAspect.update(joinPoint)
            },
            {
                ex -> ex.errorCode shouldBe FAILED_TO_GENERATE_ISSUE
            }
        )

    }

    test("오늘이 마감일이고 issueNumber가 null이 아니면 이슈를 close 상태로 변경한다.") {
        val todoListId = 1L
        val username = "testUser"
        val issueNumber = 1
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member, issueNumber)
        val gitIssue = createGitIssue(1, today, todoList)

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every { issueEventProcessor.close(member, todoList.issueNumber!!, ISSUE_OPEN) } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        updateAspect.update(joinPoint)

        verify {
            issueEventProcessor.close(member, todoList.issueNumber!!, ISSUE_OPEN)
            readMeEventProcessor.create(member)
        }
    }

    test("오늘에서 다른 날로 할 일을 수정하면 이슈를 close 상태로 변경한다.") {
        val todoListId = 1L
        val username = "testUser"
        val issueNumber = 1
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member, issueNumber)
        val gitIssue = createGitIssue(1, today.plusDays(1), todoList)

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every {
            issueEventProcessor.close(
                member,
                todoList.issueNumber!!,
                ISSUE_CLOSED
            )
        } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        updateAspect.update(joinPoint)

        verify {
            issueEventProcessor.close(member, todoList.issueNumber!!, ISSUE_CLOSED)
            readMeEventProcessor.create(member)
        }
    }

    test("마감일이 오늘이고 issueNumber가 존재할 경우 ISSUE_OPEN으로 이슈를 종료한다") {
        val todoListId = 1L
        val username = "testUser"
        val issueNumber = 1
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member, issueNumber)
        val gitIssue = createGitIssue(1, today, todoList) // == today

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every { issueEventProcessor.close(member, issueNumber, ISSUE_OPEN) } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        updateAspect.update(joinPoint)

        verify {
            issueEventProcessor.close(member, issueNumber, ISSUE_OPEN)
        }
    }

    test("마감일이 오늘이 아니고 issueNumber가 존재할 경우 ISSUE_CLOSED로 이슈를 종료한다") {
        val todoListId = 1L
        val username = "testUser"
        val issueNumber = 1
        val member = createMember(1, "token", "repo")
        val todoRequest = createTodoRequest()
        val todoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member, issueNumber)
        val gitIssue = createGitIssue(1, today.plusDays(1), todoList) // != today

        every { memberReader.getMember(username) } returns member
        every { todoReader.findTodoList(todoListId) } returns todoList
        every { gitIssueReader.findGitIssueByTodoList(todoList) } returns gitIssue
        every { issueEventProcessor.close(member, issueNumber, ISSUE_CLOSED) } just runs

        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, todoRequest, username)
        }

        updateAspect.update(joinPoint)

        verify {
            issueEventProcessor.close(member, issueNumber, ISSUE_CLOSED)
        }
    }

})
