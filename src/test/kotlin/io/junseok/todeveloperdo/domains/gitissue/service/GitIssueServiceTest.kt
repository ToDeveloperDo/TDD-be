package io.junseok.todeveloperdo.domains.gitissue.service

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueCreator
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueSaver
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class GitIssueServiceTest : BehaviorSpec({
    val gitIssueSaver = mockk<GitIssueSaver>(relaxed = true)
    val gitIssueCreator = mockk<GitIssueCreator>()
    val gitIssueService = GitIssueService(gitIssueSaver, gitIssueCreator)

    val today = LocalDate.of(2025, 3, 31)

    val member = createMember(1, "appleId")
    val todoLists = listOf(
        createMemberTodoList(1, LocalDate.now(), TodoStatus.PROCEED, member),
        createMemberTodoList(2, LocalDate.now(), TodoStatus.PROCEED, member)
    )

    val listOfIssue = todoLists.mapIndexed { index, todo ->
        createGitIssues((index + 1).toLong(), today, todoList = todo)
    }


    Given("GitIssue를 저장할 때") {
        todoLists.forEachIndexed { index, todoList ->
            every { gitIssueCreator.create(member, todoList) } returns listOfIssue[index]
        }

        When("saveGitIssue()를 호출하면") {
            gitIssueService.saveGitIssue(member, todoLists)
            Then("각 TodoList에 대해 GitIssue가 생성되어야 한다") {
                todoLists.forEach {
                    verify { gitIssueCreator.create(member, it) }
                }
            }
            Then("생성된 GitIssue 리스트가 저장되어야 한다") {
                verify(exactly = 1) { gitIssueSaver.save(listOfIssue) }
            }
        }
    }
})

fun createGitIssues(
    issueId: Long,
    deadLine: LocalDate,
    todoList: MemberTodoList,
) = GitIssue(
    issueId = issueId,
    content = "test",
    memo = "memo",
    tag = "tag",
    deadline = deadLine,
    member = createMember(1, "appleid"),
    todoList = todoList
)
