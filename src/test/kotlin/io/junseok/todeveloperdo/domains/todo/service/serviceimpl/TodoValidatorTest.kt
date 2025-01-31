package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class TodoValidatorTest : BehaviorSpec({
    val todoListRepository = mockk<TodoListRepository>()
    val todoValidator = TodoValidator(todoListRepository)
    Given("Todo를 작성한 사람이 본인이 맞는지 검증하는 경우") {
        val member = createMember(1L, "appleId")
        When("Todo작성자가 아닌 경우") {
            every { todoListRepository.existsByTodoListIdAndMember(1L, member) } returns false
            Then("ToDeveloperDoException가 발생해야한다.") {
                shouldThrow<ToDeveloperDoException> {
                    todoValidator.isWriter(1L, member)
                }.errorCode shouldBe ErrorCode.INVALID_TODOLIST
            }

            When("Todo작성자가 맞는 경우") {
                every { todoListRepository.existsByTodoListIdAndMember(1L, member) } returns true
                Then("정상적으로 동작해야한다.") {
                    shouldNotThrow<ToDeveloperDoException> {
                        todoValidator.isWriter(1L, member)
                    }
                }
            }
        }

    }
})
