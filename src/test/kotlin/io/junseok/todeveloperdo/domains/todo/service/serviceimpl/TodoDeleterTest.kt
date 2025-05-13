package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class TodoDeleterTest : BehaviorSpec({

    val todoListRepository = mockk<TodoListRepository>(relaxed = true)
    val todoDeleter = TodoDeleter(todoListRepository)

    Given("할 일 리스트를 삭제할 때") {
        val member = createMember(1L, "appleId")
        val today = LocalDate.of(2025, 5, 12)
        val todoList = createMemberTodoList(
            1L,
            today,
            TodoStatus.PROCEED,
            member
        )

        When("delete()를 호출하면") {
            todoDeleter.delete(todoList)

            Then("todoListRepository.delete()가 정확히 한 번 호출되어야 한다") {
                verify(exactly = 1) { todoListRepository.delete(todoList) }
            }
        }
    }
})
