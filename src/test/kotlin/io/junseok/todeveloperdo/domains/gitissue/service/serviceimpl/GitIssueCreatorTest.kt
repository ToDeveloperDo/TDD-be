package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.createMemberTodoList
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class GitIssueCreatorTest : BehaviorSpec({
    val gitIssueCreator = GitIssueCreator()

    Given("Member와 MemberTodoList가 주어졌을 때") {
        val member = createMember(1L, "appleId")
        val today = LocalDate.of(2025,5,12)
        val memberTodoList = createMemberTodoList(
            todoListId = 1L,
            deadline = today,
            todoStatus = TodoStatus.PROCEED,
            member = member
        )

        When("GitIssue를 생성하면") {
            val issue = gitIssueCreator.create(member, memberTodoList)

            Then("GitIssue는 MemberTodoList의 값들을 정확히 가져와야 한다") {
                issue.content shouldBe "content"
                issue.memo shouldBe "memo"
                issue.tag shouldBe "tag"
                issue.deadline shouldBe memberTodoList.deadline
                issue.member shouldBe member
                issue.todoList shouldBe memberTodoList
            }
        }
    }

})
