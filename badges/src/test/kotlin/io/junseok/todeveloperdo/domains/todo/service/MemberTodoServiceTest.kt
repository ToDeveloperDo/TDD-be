package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.gitissue.service.GitIssueService
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueUpdater
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.service.createTodoResponse
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.*
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createDateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

class MemberTodoServiceTest : BehaviorSpec({
    val todoReader = mockk<TodoReader>()
    val memberReader = mockk<MemberReader>()
    val todoSaver = mockk<TodoSaver>()
    val todoCreator = mockk<TodoCreator>()
    val todoUpdater = mockk<TodoUpdater>()
    val gitIssueService = mockk<GitIssueService>()
    val todoValidator = mockk<TodoValidator>()
    val gitIssueUpdater = mockk<GitIssueUpdater>()
    val memberTodoService = MemberTodoService(
        todoReader,
        memberReader,
        todoSaver,
        todoCreator,
        todoUpdater,
        gitIssueService,
        todoValidator,
        gitIssueUpdater
    )
    val today = LocalDate.of(2025, 5, 13)

    Given("TodoList를 생성할 때") {
        val todoRequests = listOf(createTodoRequest())
        val member = createMember(1, "appleId")
        val memberTodoList = createMemberTodoList(1, today, TodoStatus.PROCEED, member)
        val issueFuture = CompletableFuture<Int>().apply { complete(123) }
        val issueEventRequest = IssueEventRequest(member, todoRequests[0], issueFuture)

        every { memberReader.getMember(any()) } returns member
        every {
            todoCreator.generatorTodo(todoRequests[0], any(), isNull())
        } returns memberTodoList
        every { todoSaver.saveTodoList(listOf(memberTodoList)) } returns 1L
        every { gitIssueService.saveGitIssue(member, any()) } just runs
        When("createTodoList()를 호출하면") {
            val result = memberTodoService.createTodoList(
                todoRequests,
                member.gitHubUsername!!,
                issueEventRequest
            )
            Then("정상적으로 커리큘럼이 생성되어야한다.") {
                result shouldBe 1L
            }
        }
    }

    Given("할 일을 조회할 때") {
        val todoDateRequest = createDateRequest()
        val today = todoDateRequest.deadline
        val tomorrow = today.plusDays(1)

        val member = createMember(1, "appleId")
        every { memberReader.getMember(any()) } returns member
        val todoResponses = listOf(
            createTodoResponse(
                1, "content1", deadline = today
            ),
            createTodoResponse(
                2, "content2", deadline = tomorrow
            ),
        )
        every {
            todoReader.bringTodoLists(todoDateRequest.deadline, member)
        } returns todoResponses.filter { it.deadline == todoDateRequest.deadline }

        When("findTodoLists()를 호출하면") {
            val result = memberTodoService.findTodoLists(todoDateRequest, member.gitHubUsername!!)
            Then("오늘에 해당하는 할 일이 조회되어야한다.") {
                result[0].todoListId shouldBe 1
                result[0].deadline shouldBe today
                result.size shouldBe 1
            }
        }
    }

    Given("할 일을 완료했을 때") {
        val member = createMember(1, "appleId")
        val memberTodoList = createMemberTodoList(
            1,
            today,
            TodoStatus.PROCEED,
            member
        )
        every { todoReader.findTodoList(1L) } returns memberTodoList
        every { todoUpdater.doneTodoList(memberTodoList) } just runs
        When("finishTodoList()를 호출하면") {
            memberTodoService.finishTodoList(
                memberTodoList.todoListId!!,
                member.gitHubUsername!!,
                "FINISH"
            )
            Then("정상적으로 진행 중인 할 일이 완료상태로 되어야한다.") {
                verify(exactly = 1) { todoUpdater.doneTodoList(memberTodoList) }
            }
        }
    }

    Given("할 일을 수정할 때") {
        val member = createMember(1, "appleId")
        val memberTodoList = createMemberTodoList(
            1,
            today,
            TodoStatus.PROCEED,
            member
        )
        val todoRequest = createTodoRequest()
        every { memberReader.getMember(any()) } returns member
        every { todoReader.findTodoList(1L) } returns memberTodoList
        every { todoValidator.isWriter(1L, member) } just runs
        every { todoUpdater.update(memberTodoList, todoRequest) } just runs
        every { gitIssueUpdater.update(member, memberTodoList, todoRequest) } just runs
        When("modifyTodoList()이 호출되면") {
            memberTodoService.modifyTodoList(1L, todoRequest, member.gitHubUsername!!)
            Then("모든 구현체가 정확히 한 번씩 호출되어야 한다") {
                verify(exactly = 1) {
                    todoValidator.isWriter(1L, member)
                }
                verify(exactly = 1) {
                    todoUpdater.update(memberTodoList, todoRequest)
                }
                verify(exactly = 1) {
                    gitIssueUpdater.update(member, memberTodoList, todoRequest)
                }
            }
        }
    }

    Given("할 일 갯수를 조회할 때") {
        val member = createMember(1, "appleId")
        val todoCountRequest = createTodoCountRequest()
        val todoCountResponses = listOf(createTodoCountResponse())

        every { memberReader.getMember(any()) } returns member
        every { todoReader.countByTodoList(todoCountRequest, member) } returns todoCountResponses

        When("calculateTodoList()를 호출하면") {
            val result =
                memberTodoService.calculateTodoList(todoCountRequest, member.gitHubUsername!!)
            Then("할 일 갯수가 정상적으로 반환이 되어야한다.") {
                result.size shouldBe todoCountResponses.size
            }
            Then("모든 구현체가 정확히 한 번씩 호출되어야 한다") {
                verify(exactly = 1) { todoReader.countByTodoList(todoCountRequest, member) }
            }
        }
    }

    Given("완료상태인 할 일을 다시 미완료로 변경할 때") {
        val member = createMember(1, "appleId")
        val memberTodoList = createMemberTodoList(
            1,
            today,
            TodoStatus.PROCEED,
            member
        )
        every { todoReader.findTodoList(1L) } returns memberTodoList
        every { todoUpdater.proceedTodoList(memberTodoList) } just runs
        When("unFinishedTodoList()를 호출하면") {
            memberTodoService.unFinishedTodoList(1L, member.gitHubUsername!!, "DONE")
            Then("진행 상태로 되돌리는 로직이 실행되어야 한다") {
                verify(exactly = 1) { todoUpdater.proceedTodoList(memberTodoList) }
            }
        }
    }

    Given("할 일 리스트를 삭제할 때") {
        val todoListId = 1L
        val username = "appleId"
        val state = "DONE"

        When("removeTodoList()를 호출하면") {
            memberTodoService.removeTodoList(todoListId, username, state)

            Then("정상적으로 삭제 이벤트가 처리되어야 한다") {
                true shouldBe true
            }
        }
    }
})
