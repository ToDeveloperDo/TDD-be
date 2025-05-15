package io.junseok.todeveloperdo.aop.annotation.delete

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoDeleter
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.junseok.todeveloperdo.event.EventProcessor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.aspectj.lang.JoinPoint
import java.time.LocalDate

class DeleteTodoAspectTest : FunSpec({
    val eventProcessor = mockk<EventProcessor>()
    val memberReader = mockk<MemberReader>()
    val todoDeleter = mockk<TodoDeleter>()
    val todoReader = mockk<TodoReader>()
    val deleteTodoAspect = DeleteTodoAspect(
        eventProcessor,
        memberReader,
        todoDeleter,
        todoReader
    )

    test("afterCloseIssue()는 할 일을 삭제하고 이슈를 종료 이벤트로 전파한다") {
        val todoListId = 1L
        val userName = "username"
        val state = "state"
        val member = createMember(1, "appleId", "repo")
        val today = LocalDate.of(2025, 5, 13)
        val memberTodoList = createMemberTodoList(
            todoListId = 1L,
            deadline = today,
            todoStatus = TodoStatus.PROCEED,
            member = member
        )
        val joinPoint = mockk<JoinPoint> {
            every { args } returns arrayOf(todoListId, userName, state)
        }
        every { memberReader.getMember(userName) } returns member
        every { todoReader.findTodoList(todoListId) } returns memberTodoList

        every { todoDeleter.delete(memberTodoList) } just runs
        every { eventProcessor.closeIssueWithReadMe(member, memberTodoList, state) } just runs

        deleteTodoAspect.afterCloseIssue(joinPoint)

        verify {
            todoDeleter.delete(memberTodoList)
            eventProcessor.closeIssueWithReadMe(member, memberTodoList, state)
        }
    }
})
