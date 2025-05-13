package io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class GitHubIssueCreatorTest : FunSpec({
    val gitHubIssueCreator = GitHubIssueCreator()

    test("TodoCreate를 기반으로 GitHubIssuesRequest를 생성해야 한다") {
        val member = createMember(1, "appleId")
        val todoCreate = createTodoRequest().toTodoCreate(member)

        val result = gitHubIssueCreator.createIssueTemplate(todoCreate)

        result.title shouldBe "2025-05-13 / content"
        result.body shouldContain "TODO : content"
        result.body shouldContain "MEMO : memo"
        result.body shouldContain "TAG : tag"
        result.assignees shouldContainExactly listOf("username")
    }
})
