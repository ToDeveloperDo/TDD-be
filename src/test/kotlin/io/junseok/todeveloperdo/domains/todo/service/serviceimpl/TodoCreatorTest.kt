package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class TodoCreatorTest : BehaviorSpec({
    val todoCreator = TodoCreator()

    Given("TodoRequest와 Member를 입력으로 받을 때") {
        val todoRequest = TodoRequest(
            content = "할 일 작성",
            memo = "상세 메모",
            tag = "공부",
            deadline = LocalDate.of(2025, 5, 12)
        )
        val member = createMember(1L, "appleId")

        When("issueNumber가 null이 아닐 때") {
            val issueNumber = 123
            val result = todoCreator.generatorTodo(todoRequest, member, issueNumber)

            Then("모든 필드가 정확히 복사되어야 한다") {
                result.content shouldBe todoRequest.content
                result.memo shouldBe todoRequest.memo
                result.tag shouldBe todoRequest.tag
                result.deadline shouldBe todoRequest.deadline
                result.todoStatus shouldBe TodoStatus.PROCEED
                result.issueNumber shouldBe issueNumber
                result.member shouldBe member
            }
        }

        When("issueNumber가 null일 때"){
            val result = todoCreator.generatorTodo(todoRequest, member)

            Then("issueNumber는 null이어야 한다") {
                result.issueNumber shouldBe null
                result.todoStatus shouldBe TodoStatus.PROCEED
            }
        }
    }

})
