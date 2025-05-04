package io.junseok.todeveloperdo.presentation.membertodolist

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.domains.memberfriend.service.createTodoResponse
import io.junseok.todeveloperdo.domains.todo.service.MemberTodoService
import io.junseok.todeveloperdo.global.rabbitmq.RabbitMQProducer
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoDateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequests
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.scheduler.FcmScheduler
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.setAuthorization
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDate

class MemberTodoControllerTest : FunSpec({
    val memberTodoService = mockk<MemberTodoService>()
    val fcmScheduler = mockk<FcmScheduler>()
    //val rabbitMQProducer = mockk<RabbitMQProducer>()
    val memberTodoController = MemberTodoController(
        memberTodoService,
        fcmScheduler,
        //rabbitMQProducer
    )
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(memberTodoController)
        .apply<StandaloneMockMvcBuilder>(
            MockMvcRestDocumentation.documentationConfiguration(
                restDocumentation
            )
        )
        .build()

    beforeSpec {
        ObjectMappers.objectMapper = ObjectMapper()
            .registerModules(KotlinModule.Builder().build())
            .registerModules(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        restDocumentation.beforeTest(javaClass, "MemberTodoController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    test("할 일 등록 API") {
        val todoRequests = TodoRequests(todos = listOf(createTodoRequest()))
        every { memberTodoService.createTodoList(todoRequests.todos, any()) } returns 1

        val mvcResult = mockMvc.perform(
            post(TODO_PATH)
                .setAuthorization()
                .contentType(MediaType.APPLICATION_JSON)
                .content(todoRequests.toRequest())
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "create-todo",
                    authorizationHeader(),
                    requestFields(
                        "todos" typeOf ARRAY means "할 일 리스트",
                        "todos[].content" typeOf STRING means "할 일 내용",
                        "todos[].memo" typeOf STRING means "메모",
                        "todos[].tag" typeOf STRING means "태그",
                        "todos[].deadline" typeOf STRING means "마감일 (yyyy-MM-dd)"
                    ),
                )
            ).andReturn()
        mvcResult.toResponse<Long>() shouldBe 1L
    }

    test("해당 요일에 있는 할 일 목록 조회 API") {
        val today = LocalDate.now()
        val createDateRequest = createDateRequest()
        val todoResponses = listOf(
            createTodoResponse(1L, "", today),
        )
        every { memberTodoService.findTodoLists(any(), any()) } returns todoResponses

        val mvcResult = mockMvc.perform(
            post(TODO_PATH + "list")
                .setAuthorization()
                .contentType(MediaType.APPLICATION_JSON)
                .content(createDateRequest.toRequest())
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "find-todoList",
                    authorizationHeader(),
                    requestFields(
                        "deadline" typeOf STRING means "마감일 (yyyy-MM-dd)"
                    ),
                    responseFields(
                        "todoListId" arrayTypeOf NUMBER means "할 일 Id",
                        "content" arrayTypeOf STRING means "할 일 내용",
                        "memo" arrayTypeOf STRING means "메모",
                        "tag" arrayTypeOf STRING means "태크",
                        "deadline" arrayTypeOf ARRAY means "마감일 (yyyy-MM-dd)",
                        "todoStatus" arrayTypeOf STRING means "할 일 상태"
                    )
                )
            ).andReturn()

        mvcResult.toResponse<List<TodoResponse>>() shouldBe todoResponses
    }

    test("한 일 체크 API") {
        every { memberTodoService.finishTodoList(any(), any(), any()) } just runs
        mockMvc.perform(
            patch(TODO_PATH + "done/{todoListId}", 1L)
                .setAuthorization()
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "done-todoList",
                    authorizationHeader(),
                    pathParameters(
                        "todoListId" parameterTypeOf NUMBER parameterMeans "완료한 TodoListId"
                    )
                )
            )
        verify(exactly = 1) { memberTodoService.finishTodoList(any(), any(), any()) }
    }

    test("todoList 수정 API") {
        every { memberTodoService.modifyTodoList(any(), any(), any()) } just runs

        val todoRequest = createTodoRequest()
        mockMvc.perform(
            patch(TODO_PATH + "change/{todoListId}", 1L)
                .setAuthorization()
                .content(todoRequest.toRequest())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "modify-todoList",
                    authorizationHeader(),
                    requestFields(
                        "content" typeOf STRING means "할 일 내용",
                        "memo" typeOf STRING means "메모",
                        "tag" typeOf STRING means "태그",
                        "deadline" typeOf STRING means "마감일 (yyyy-MM-dd)"
                    ),
                    pathParameters(
                        "todoListId" parameterTypeOf NUMBER parameterMeans "수정할 TodoListId"
                    )
                )
            )
        verify(exactly = 1) { memberTodoService.modifyTodoList(any(), any(), any()) }
    }

    test("todoList 삭제 API") {
        every { memberTodoService.removeTodoList(any(), any(), any()) } just runs
        mockMvc.perform(
            delete("$TODO_PATH{todoListId}", 1L)
                .setAuthorization()
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "remove-todoList",
                    authorizationHeader(),
                    pathParameters(
                        "todoListId" parameterTypeOf NUMBER parameterMeans "삭제할 TodoListId"
                    )
                )
            )

        verify(exactly = 1) { memberTodoService.removeTodoList(any(), any(), any()) }
    }

    test("달에 할 일 목록 갯수 세오는 API") {
        val todoCountRequest = createTodoCountRequest()
        val countResponses = listOf(
            createTodoCountResponse(),
            createTodoCountResponse()
        )
        every {
            memberTodoService.calculateTodoList(
                todoCountRequest,
                any()
            )
        } returns countResponses

        val mvcResult = mockMvc.perform(
            post(TODO_PATH + "count")
                .setAuthorization()
                .contentType(MediaType.APPLICATION_JSON)
                .content(todoCountRequest.toRequest())
        ).andExpect(status().isOk)
            .andDo(
                document(
                    "calculate-todoList",
                    authorizationHeader(),
                    requestFields(
                        "year" typeOf NUMBER means "조회하는 연(년)도",
                        "month" typeOf NUMBER means "조회하는 달"
                    ),
                    responseFields(
                        "deadline" arrayTypeOf ARRAY means "마감기한 일",
                        "count" arrayTypeOf NUMBER means "할 일 갯수"
                    )
                )
            ).andReturn()

        mvcResult.toResponse<List<TodoCountResponse>>() shouldBe countResponses
    }

    test("할 일을 미진행 -> 진행으로 변경하는 API") {
        every { memberTodoService.unFinishedTodoList(any(), any(), any()) } just runs
        mockMvc.perform(
            patch(TODO_PATH+"proceed/{todoListId}",1L)
                .setAuthorization()
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "change-todoList-status",
                    authorizationHeader(),
                    pathParameters(
                        "todoListId" parameterTypeOf NUMBER parameterMeans "진행할 TodoListId"
                    )
                )
            )
        verify(exactly = 1) { memberTodoService.unFinishedTodoList(any(),any(),any()) }
    }
}) {
    companion object {
        const val TODO_PATH = "/api/todo/"
    }
}

fun createTodoRequest() = TodoRequest(
    content = "content",
    memo = "memo",
    tag = "tag",
    deadline = LocalDate.now()
)

fun createTodoCountRequest() = TodoCountRequest(
    year = 2025,
    month = 5
)

fun createDateRequest() = TodoDateRequest(deadline = LocalDate.now())

fun createTodoCountResponse() = TodoCountResponse(
    deadline = LocalDate.now(),
    count = 10
)
