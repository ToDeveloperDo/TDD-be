package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoQueryRepository
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class TodoReaderTest : BehaviorSpec({
    val todoListRepository = mockk<TodoListRepository>()
    val todoQueryRepository = mockk<TodoQueryRepository>()
    val todoReader = TodoReader(todoListRepository, todoQueryRepository)

    Given("해당 요일에 해당하는 ToDo목록을 불러올 때") {
        val setUpData = SetUpData.listData()

        every {
            todoListRepository.findByDeadlineAndMember(
                setUpData.currentDate,
                setUpData.member
            )
        } returns setUpData.todoLists
        When("요일이 정상적으로 넘어왔을 때") {
            val todoResponses = todoReader.bringTodoLists(setUpData.currentDate, setUpData.member)
            Then("TodoResponse Dto타입으로 반환이 되어야한다.") {
                todoResponses.shouldBeInstanceOf<List<TodoResponse>>()
            }
            Then("정보가 올바르게 반환되어야한다.") {
                todoResponses[0].todoListId shouldBe setUpData.todoLists.get(0).todoListId
                todoResponses[1].todoListId shouldBe setUpData.todoLists.get(1).todoListId
                todoResponses[2].todoListId shouldBe setUpData.todoLists.get(2).todoListId
            }
        }
    }

    Given("해당 요일에 진행 중인 Todo목록을 불러올 때") {
        val setUpData = SetUpData.listData()
        every {
            todoListRepository.findByDeadlineAndTodoStatusAndMember(
                setUpData.currentDate,
                TodoStatus.PROCEED,
                setUpData.member
            )
        } returns setUpData.todoLists
        When("요일이 정상적으로 넘어왔을 때") {
            val todoResponses = todoReader.bringProceedTodoLists(
                setUpData.currentDate, setUpData.member
            )
            Then("TodoResponse Dto타입으로 반환이 되어야한다.") {
                todoResponses.shouldBeInstanceOf<List<TodoResponse>>()
            }
            Then("진행 중인 Todo만 불러와야한다.") {
                todoResponses.all { it.todoStatus == TodoStatus.PROCEED } shouldBe true
            }
        }
    }

    Given("친구 할 일 목록 일주일 치 Todo목록을 불러올 때") {
        val setUpData = SetUpData.listData()
        val weeksDay = setUpData.currentDate.minusWeeks(1)
        every {
            todoListRepository.findByMemberAndDeadlineBetween(
                setUpData.member, weeksDay, setUpData.currentDate
            )
        } returns setUpData.todoLists
        When("현재 날짜가 정상적으로 넘어온 경우") {
            val result = todoReader.bringTodoListForWeek(
                setUpData.currentDate, setUpData.member
            )
            Then("List<DeadlineTodoResponse>으로 반환이 되어야한다.") {
                result.shouldBeInstanceOf<List<DeadlineTodoResponse>>()
            }

            Then("반환된 리스트는 날짜별로 그룹화 되어야 한다.") {
                result.size shouldBe 3
                result.map { it.deadline } shouldContainExactly setUpData.todoLists.map { it.deadline }
                    .distinct()
            }

            Then("각 그룹 안의 todoResponse는 TodoResponse로 변환되어야 한다.") {
                result.forEach { deadlineTodoResponse ->
                    deadlineTodoResponse.todoResponse.shouldBeInstanceOf<List<TodoResponse>>()
                }
            }
        }
    }
})

data class SetUpData(
    val member: Member,
    val currentDate: LocalDate,
    val todoLists: List<MemberTodoList>,
) {
    companion object {
        fun listData(): SetUpData {
            val member = createMember(1L, "appleId")
            val currentDate = LocalDate.now()
            val todoLists = listOf(
                createMemberTodoList(1L, currentDate.minusWeeks(2), TodoStatus.PROCEED, member),
                createMemberTodoList(3L, currentDate.minusWeeks(3), TodoStatus.PROCEED, member),
                createMemberTodoList(3L, currentDate.minusWeeks(4), TodoStatus.PROCEED, member)
            )
            return SetUpData(member, currentDate, todoLists)
        }
    }
}

fun createMemberTodoList(
    todoListId: Long,
    deadline: LocalDate,
    todoStatus: TodoStatus,
    member: Member,
) = MemberTodoList(
    todoListId = todoListId,
    content = "content",
    memo = "memo",
    tag = "tag",
    deadline = deadline,
    todoStatus = todoStatus,
    member = member
)