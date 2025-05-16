package io.junseok.todeveloperdo.presentation.memberfriend

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.junseok.todeveloperdo.domains.memberfriend.service.MemberFriendService
import io.junseok.todeveloperdo.domains.memberfriend.service.createMemberResponse
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.request.FriendNameRequest
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.util.ObjectMappers
import io.junseok.todeveloperdo.util.dsl.*
import io.junseok.todeveloperdo.util.setAuthorization
import io.junseok.todeveloperdo.util.toRequest
import io.junseok.todeveloperdo.util.toResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import java.time.LocalDate

@WebMvcTest(MemberFriendController::class)
@AutoConfigureRestDocs
class MemberFriendControllerTest : FunSpec({
    val memberFriendService = mockk<MemberFriendService>()
    val memberFriendController = MemberFriendController(memberFriendService)
    val restDocumentation = ManualRestDocumentation()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(memberFriendController)
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
        restDocumentation.beforeTest(javaClass, "MemberFriendController")
    }
    afterSpec {
        restDocumentation.afterTest()
    }

    test("친구 목록 조회 API호출") {
        val friendResponses = listOf(
            createMemberFriendResponse(1),
            createMemberFriendResponse(2)
        )
        every { memberFriendService.findMemberFriendList(any()) } returns friendResponses
        val mvcResult = mockMvc.perform(
            get(PATH)
                .setAuthorization("username")
        ).andExpect(status().isOk)
            .andDocument(
                "Follow member-Friend",
                authorizationHeader(),
                responseFields(
                    "memberId" arrayTypeOf NUMBER means "사용자 ID",
                    "friendUsername" arrayTypeOf STRING means "사용자 깃허브 닉네임",
                    "friendGitUrl" arrayTypeOf STRING means "사용자 깃허브 URL",
                    "avatarUrl" arrayTypeOf STRING means "사용자 깃허브 프로필URL"
                )
            ).andReturn()

        mvcResult.toResponse<List<MemberFriendResponse>>() shouldBe friendResponses
        verify(exactly = 1) { memberFriendService.findMemberFriendList(any()) }
    }

    test("나에게 온 친구 요청 목록 조회 API") {
        val friendResponses = listOf(
            createMemberFriendResponse(1),
            createMemberFriendResponse(2)
        )
        every { memberFriendService.findWaitFriends(any()) } returns friendResponses

        val mvcResult = mockMvc.perform(
            get(PATH + "request-list")
                .setAuthorization()
        ).andExpect(status().isOk)
            .andDocument(
                "request-Friend-list",
                authorizationHeader(),
                responseFields(
                    "memberId" arrayTypeOf NUMBER means "사용자 ID",
                    "friendUsername" arrayTypeOf STRING means "사용자 깃허브 닉네임",
                    "friendGitUrl" arrayTypeOf STRING means "사용자 깃허브 URL",
                    "avatarUrl" arrayTypeOf STRING means "사용자 깃허브 프로필URL"
                )
            )
            .andReturn()
        mvcResult.toResponse<List<MemberFriendResponse>>() shouldBe friendResponses
        verify(exactly = 1) { memberFriendService.findWaitFriends(any()) }
    }

    test("단일 친구 조회 API") {
        val friendResponse = createMemberFriendResponse(1)
        every {
            memberFriendService.findMemberFriend(
                friendResponse.memberId
            )
        } returns friendResponse

        val mvcResult = mockMvc.perform(
            get("$PATH{memberId}", friendResponse.memberId)
                .setAuthorization()
        )
            .andExpect(status().isOk)
            .andDocument(
                "single-friend",
                pathParameters(
                    "memberId" parameterTypeOf NUMBER parameterMeans "조회할 사용자 ID"
                ),
                authorizationHeader(),
                responseFields(
                    "memberId" typeOf NUMBER means "사용자 ID",
                    "friendUsername" typeOf STRING means "사용자 깃허브 닉네임",
                    "friendGitUrl" typeOf STRING means "사용자 깃허브 URL",
                    "avatarUrl" typeOf STRING means "사용자 깃허브 프로필URL"
                )
            ).andReturn()
        mvcResult.toResponse<MemberFriendResponse>() shouldBe friendResponse
    }

    test("친구 추가 요청 API") {
        every { memberFriendService.registerFriend(1L, any()) } just runs
        mockMvc.perform(
            get(PATH + "add/{friendId}", 1L)
                .setAuthorization()
        )
            .andExpect(status().isOk)
            .andDocument(
                "request-friend",
                pathParameters(
                    "friendId" parameterTypeOf NUMBER parameterMeans "요청할 사용자 ID"
                ),
                authorizationHeader()
            )

        verify(exactly = 1) { memberFriendService.registerFriend(1L, any()) }
    }

    test("친구 삭제(언팔) API") {
        every { memberFriendService.deleteFriend(1L, any(), any()) } just runs
        mockMvc.perform(
            delete("$PATH{friendId}", 1L)
                .setAuthorization()
                .queryParam("type", "FOLLOW")
        ).andExpect(status().isOk)
            .andDocument(
                "unfollow-friend",
                authorizationHeader(),
                pathParameters(
                    "friendId" parameterTypeOf NUMBER parameterMeans "언팔할 사용자 ID"
                ),
                requestParameters(
                    "type" parameterTypeOf NUMBER requestParamMeans "FriendStatus 상태값"
                )
            )

        verify(exactly = 1) { memberFriendService.deleteFriend(1L, any(), any()) }
    }

    test("친구 요청 수락 API") {
        every { memberFriendService.approveRequest(1L, any()) } just runs

        mockMvc.perform(
            get(PATH + "accept/{friendId}", 1L)
                .setAuthorization()
        ).andExpect(status().isOk)
            .andDocument(
                "accept-friend",
                pathParameters(
                    "friendId" parameterTypeOf NUMBER parameterMeans "친구요청을 수락할 사용자 ID"
                ),
                authorizationHeader()
            )

        verify(exactly = 1) { memberFriendService.approveRequest(1L, any()) }
    }

    test("내가 보낸 요청 목록 조회 API") {
        val friendResponses = listOf(
            createMemberFriendResponse(1),
            createMemberFriendResponse(2)
        )
        every { memberFriendService.findSendRequestList(any()) } returns friendResponses

        val mvcResult = mockMvc.perform(
            get(PATH + "send-list")
                .setAuthorization()
        ).andExpect(status().isOk)
            .andDocument(
                "find-send-list",
                authorizationHeader(),
                responseFields(
                    "memberId" arrayTypeOf NUMBER means "사용자 ID",
                    "friendUsername" arrayTypeOf STRING means "사용자 깃허브 닉네임",
                    "friendGitUrl" arrayTypeOf STRING means "사용자 깃허브 URL",
                    "avatarUrl" arrayTypeOf STRING means "사용자 깃허브 프로필URL"
                )
            ).andReturn()

        mvcResult.toResponse<List<MemberFriendResponse>>() shouldBe friendResponses
    }
    test("친구인 다른 사람 할 일 목록 조회 API") {
        val todoResponses =
            listOf(createDeadLineResponse(), createDeadLineResponse())
        every { memberFriendService.searchFriendTodo(1L, any()) } returns todoResponses

        val mvcResult = mockMvc.perform(
            get(PATH + "lookup/todolist/{friendId}", 1L)
                .setAuthorization()
                .characterEncoding("UTF-8")
        )
            .andExpect(status().isOk)
            .andDocument(
                "search-friend-todolist",
                pathParameters(
                    "friendId" parameterTypeOf NUMBER parameterMeans "조회할 사용자 Id"
                ),
                authorizationHeader()
            ).andReturn()
        mvcResult.toResponse<List<DeadlineTodoResponse>>() shouldBe todoResponses
    }

    test("친구 깃허브 이름으로 친구 검색 API") {
        val memberResponse = createMemberResponse(1L, "apple")
        every { memberFriendService.getGitFriend(any(), any()) } returns memberResponse

        val mvcResult = mockMvc.perform(
            post(PATH + "search")
                .setAuthorization()
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    FriendNameRequest("friendName").toRequest()
                )
        ).andExpect(status().isOk)
            .andDocument(
                "find-by-gitName",
                requestFields(
                    "gitUserName" typeOf STRING means "검색하려는 사용자 Git 닉네임"
                ),
                responseFields(
                    "memberId" typeOf NUMBER means "사용자 ID",
                    "username" typeOf STRING means "사용자 Git 닉네임",
                    "avatarUrl" typeOf STRING means "Git 프로필 URL",
                    "gitUrl" typeOf STRING means "사용자 gitUrl",
                    "friendStatus" typeOf STRING means "친구상태"
                ),
                authorizationHeader()
            ).andReturn()

        mvcResult.toResponse<MemberResponse>() shouldBe memberResponse
    }
}) {
    companion object {
        const val PATH: String = "/api/member-friend/"
    }
}

fun createMemberFriendResponse(memberId: Long) = MemberFriendResponse(
    memberId = memberId,
    friendUsername = "friend",
    friendGitUrl = "gitUrl",
    avatarUrl = "avatarUrl"
)

fun createDeadLineResponse() = DeadlineTodoResponse(
    deadline = LocalDate.now(),
    todoResponse =
    listOf(
        TodoResponse(
            todoListId = 1L,
            content = "content",
            memo = "",
            tag = "",
            deadline = LocalDate.now(),
            todoStatus = TodoStatus.PROCEED
        )
    )
)
