package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class GitIssueUpdaterTest : BehaviorSpec({
    val issueReader = mockk<GitIssueReader>()
    val gitIssueUpdater = GitIssueUpdater(issueReader)

    val today = LocalDate.of(2025, 3, 31)
    val updatedDeadline = today.minusWeeks(1)

    val member = createMember(1, "appleId")
    val todoList = createMemberTodoList(1, LocalDate.now(), TodoStatus.PROCEED, member)
    val todoRequest = TodoRequest(
        content = "modify content",
        memo = "modify memo",
        tag = "modify tag",
        deadline = updatedDeadline
    )
    val gitIssue = createGitIssue(1, today, todoList)

    Given("수정할 GitIssue와 수정 요청이 존재할 때") {
        every { issueReader.findGitIssueByTodoList(todoList) } returns gitIssue

        When("GitIssueUpdater.update()를 호출하면") {
            gitIssueUpdater.update(member, todoList, todoRequest)
            Then("GitIssue의 내용이 TodoRequest로 변경되어야 한다") {
                with(gitIssue) {
                    memo shouldBe "modify memo"
                    tag shouldBe "modify tag"
                    deadline shouldBe today.minusWeeks(1)
                    content shouldBe "modify content"
                }
            }
        }
    }
})
