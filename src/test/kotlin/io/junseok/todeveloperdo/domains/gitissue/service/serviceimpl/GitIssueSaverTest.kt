package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class GitIssueSaverTest : BehaviorSpec({
    val gitIssueRepository = mockk<GitIssueRepository>()
    val gitIssueSaver = GitIssueSaver(gitIssueRepository)

    Given("여러 개의 GitIssue가 주어졌을 때") {
        val member = createMember(1L, "appleId")
        val today = LocalDate.of(2025, 5, 12)
        val todoList = createMemberTodoList(1L, today, TodoStatus.PROCEED, member)
        val gitIssues = listOf(createGitIssue(1, today, todoList))

        every { gitIssueRepository.saveAll(gitIssues) } returns gitIssues

        When("save()를 호출하면") {
            val result = gitIssueSaver.save(gitIssues)
            Then("GitIssueRepository의 saveAll이 호출되어야 한다") {
                verify(exactly = 1) { gitIssueRepository.saveAll(gitIssues) }
            }
            Then("정상적으로 저장된 GitIssue 리스트가 반환되어야 한다") {
                result shouldBe gitIssues
            }
        }
    }
})
