package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class GitIssueReaderTest : BehaviorSpec({
    val gitIssueRepository = mockk<GitIssueRepository>()
    val gitIssueReader = GitIssueReader(gitIssueRepository)

    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val tomorrow = today.plusDays(1)

    val todoList =
        createMemberTodoList(
            1L,
            today.minusWeeks(2),
            TodoStatus.PROCEED,
            createMember(1, "appleid")
        )

    fun stubGitIssuesWithMixedDates(): List<GitIssue> = listOf(
        createGitIssue(1, today, todoList),
        createGitIssue(2, today, todoList),
        createGitIssue(3, yesterday, todoList),
        createGitIssue(4, tomorrow, todoList)
    )


    Given("오늘할 일인 GitIssue를 찾을 때") {
        val mixedIssues = stubGitIssuesWithMixedDates()
        every { gitIssueRepository.findByDeadlineList(today) } returns
                mixedIssues.filter { it.deadline == today }

        When("오늘자 할 일 이슈만 조회하면") {
            val createList = gitIssueReader.findGitIssueList()

            Then("오늘 할 일의 결과만 조회되어야한다.") {
                createList shouldHaveSize 2
            }
            Then("모든 이슈의 마감일은 오늘이어야 한다.") {
                createList.all { it.deadline == today } shouldBe true
            }
            Then("오늘 할 일이 아닌 이슈는 조회되선 안된다.") {
                createList.any { it.deadline != today } shouldBe false
            }
        }
    }

    Given("사용자 TodoList가 존재할 때") {
        val expectedIssue = createGitIssue(1L, today, todoList)

        every { gitIssueRepository.findByTodoList(todoList) } returns expectedIssue
        When("사용자 TodoList로 조회할 때") {
            val result = gitIssueReader.findGitIssueByTodoList(todoList)
            Then("Todo와 매칭되는 이슈가 조회되어야한다.") {
                result.issueId shouldBe 1
                result.todoList shouldBe todoList
                result.content shouldBe "test"
            }
        }
    }
})

fun createGitIssue(
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