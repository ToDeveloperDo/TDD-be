package io.junseok.todeveloperdo.aop.annotation.close

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.junseok.todeveloperdo.event.EventProcessor
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.aspectj.lang.JoinPoint
import java.time.LocalDate

class HandleIssueAspectTest : FunSpec({
    val eventProcessor = mockk<EventProcessor>()
    val memberReader = mockk<MemberReader>()
    val todoReader = mockk<TodoReader>()
    val handleIssueAspect = HandleIssueAspect(eventProcessor, memberReader, todoReader)

    test("afterCloseIssue()는 할 일을 조회하고 이슈 종료 이벤트를 전파한다") {
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

        every { eventProcessor.closeIssueWithReadMe(member, memberTodoList, state) } just runs

        handleIssueAspect.afterCloseIssue(joinPoint)

        verify {
            todoReader.findTodoList(todoListId)
            eventProcessor.closeIssueWithReadMe(member, memberTodoList, state)
        }
    }
})
