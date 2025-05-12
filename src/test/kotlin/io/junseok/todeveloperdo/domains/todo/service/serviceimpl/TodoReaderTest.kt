package io.junseok.todeveloperdo.domains.todo.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode.NOT_EXIST_TODOLIST
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class TodoReaderTest : BehaviorSpec({
    val todoListRepository = mockk<TodoListRepository>()
    val todoReader = TodoReader(todoListRepository)
    val today = LocalDate.of(2025, 5, 6)

    Given("해당 요일에 해당하는 ToDo목록을 불러올 때") {
        val setUpData = SetUpData.listData(today)

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
        val setUpData = SetUpData.listData(today)
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
        val setUpData = SetUpData.listData(today)
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

    Given("할 일 상태에 따라 할 일 목록을 조회할 때") {
        TodoStatus.entries.forEach { status ->
            When("상태가 $status 인 경우") {
                val setUpData = SetUpData.listData(today, status)

                every {
                    todoListRepository.findAllByDeadlineAndTodoStatus(today, status)
                } returns setUpData.todoLists

                val todoLists = todoReader.findTodoListByTodoStatus(today, status)

                Then("해당 상태의 할 일 목록이 반환되어야 한다") {
                    todoLists.size shouldBe setUpData.todoLists.size
                    todoLists.all { it.todoStatus == status } shouldBe true
                }
            }
        }
    }

    Given("할 일을 조회할 때") {
        val member = createMember(1, "appleId")
        val memberTodoList = createMemberTodoList(1L, today, TodoStatus.DONE, member)
        When("할 일이 존재하는 경우") {
            every { todoListRepository.findByIdOrNull(1L) } returns memberTodoList
            val result = todoReader.findTodoList(1L)
            Then("정상적으로 조회가 된다.") {
                result shouldBe memberTodoList
            }
        }

        When("할 일이 존재하지 않는 경우") {
            every { todoListRepository.findByIdOrNull(1L) } returns null
            Then("NOT_EXIST_TODOLIST 에러가 반환된다.") {
                throwsWith<ToDeveloperDoException>(
                    {
                        todoReader.findTodoList(1L)
                    },
                    { ex ->
                        ex.errorCode shouldBe NOT_EXIST_TODOLIST
                    }
                )
            }
        }
    }

    Given("등록된 할 일의 갯수를 불러올 때") {
        val countRequest = createTodoCountRequest()
        val member = createMember(1, "appleId")
        val responseList = listOf(createTodoCountResponse())
        every {
            todoListRepository.findAllByMonthAndYearAndMember(
                countRequest.month,
                countRequest.year,
                member
            )
        } returns responseList
        When("countByTodoList()를 호출하면") {
            val result = todoReader.countByTodoList(countRequest, member)
            Then("정상적으로 갯수가 반환되어야한다.") {
                result shouldBe responseList
                result.size shouldBe responseList.size
            }
            Then("쿼리 메서드가 정확히 1번 호출되어야 한다.") {
                verify(exactly = 1) {
                    todoListRepository.findAllByMonthAndYearAndMember(
                        countRequest.month,
                        countRequest.year,
                        member
                    )
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
        fun listData(date: LocalDate, todoStatus: TodoStatus = TodoStatus.PROCEED): SetUpData {
            val member = createMember(1L, "appleId")
            val todoLists = listOf(
                createMemberTodoList(1L, date.minusWeeks(2), todoStatus, member),
                createMemberTodoList(3L, date.minusWeeks(3), todoStatus, member),
                createMemberTodoList(3L, date.minusWeeks(4), todoStatus, member)
            )
            return SetUpData(member, date, todoLists)
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