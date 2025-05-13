package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class TodoSaverTest : BehaviorSpec({
    val todoListRepository = mockk<TodoListRepository>()
    val todoSaver = TodoSaver(todoListRepository)

    Given("여러 개의 MemberTodoList를 저장할 때") {
        val member = createMember(1L, "appleId")
        val today = LocalDate.of(2025, 5, 12)
        val todo1 = createMemberTodoList(1L, today, TodoStatus.PROCEED, member)
        val todo2 = createMemberTodoList(2L, today, TodoStatus.PROCEED, member)
        val todoList = listOf(todo1, todo2)

        every { todoListRepository.saveAll(todoList) } returns todoList

        When("saveTodoList()를 호출하면") {
            val result = todoSaver.saveTodoList(todoList)

            Then("saveAll()이 정확히 1번 호출되어야 한다") {
                verify(exactly = 1) { todoListRepository.saveAll(todoList) }
            }

            Then("첫 번째 저장 객체의 ID를 반환해야 한다") {
                result shouldBe 1L
            }
        }
    }
})
