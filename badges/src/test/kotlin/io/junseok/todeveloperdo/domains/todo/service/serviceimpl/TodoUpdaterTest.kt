package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import java.time.LocalDate

class TodoUpdaterTest : BehaviorSpec({
    val todoUpdater = TodoUpdater()
    Given("할 일을 수정할 때") {
        val member = createMember(1L, "appleId")
        val todoList =
            createMemberTodoList(1L, LocalDate.now(), TodoStatus.PROCEED, member)
        val todoRequest = TodoRequest(
            content = "modify content",
            memo = "modify memo",
            tag = "modify tag",
            deadline = LocalDate.now().minusWeeks(1),
        )
        When("수정할 컬럼들이 정상적으로 들어온 경우") {
            todoUpdater.update(todoList, todoRequest)
            Then("할 일이 정상적으로 수정되어야한다.") {
                todoList.content shouldBe todoRequest.content
                todoList.memo shouldBe todoRequest.memo
                todoList.tag shouldBe todoRequest.tag
                todoList.deadline shouldBe todoRequest.deadline
            }
        }
    }

    Given("할 일을 완료했을 때") {
        val member = createMember(1L, "appleId")
        val todoList =
            createMemberTodoList(1L, LocalDate.now(), TodoStatus.PROCEED, member)
        When("doneTodoList()이 실행되면") {
            todoUpdater.doneTodoList(todoList)
            Then("TodoStatus가 DONE으로 변경되어야한다.") {
                todoList.todoStatus shouldBe TodoStatus.DONE
            }
        }
    }


    Given("완료했던 할 일을 다시 진행 중으로 변경할 때") {
        val member = createMember(1L, "appleId")
        val todoList =
            createMemberTodoList(1L, LocalDate.now(), TodoStatus.DONE, member)
        When("proceedTodoList()이 실행되면") {
            todoUpdater.proceedTodoList(todoList)
            Then("TodoStatus가 PROCEED으로 변경되어야한다.") {
                todoList.todoStatus shouldBe TodoStatus.PROCEED
            }
        }
    }

    Given("이슈 번호를 수정할 때") {
        val issueNumber = 123
        val member = createMember(1L, "appleId")
        val todoList =
            createMemberTodoList(1L, LocalDate.now(), TodoStatus.PROCEED, member)
        When("modifyIssueNumber()를 호출하면") {
            todoUpdater.modifyIssueNumber(issueNumber, todoList)
            Then("이슈 번호가 정상적으로 수정되어야한다.") {
                todoList.issueNumber shouldBe issueNumber
            }
        }
    }
})
