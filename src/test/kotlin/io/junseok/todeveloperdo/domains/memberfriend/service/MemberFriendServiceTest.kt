package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberProcessor
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.getFriendOf
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.*
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class MemberFriendServiceTest : BehaviorSpec({
    val memberReader = mockk<MemberReader>()
    val memberFriendValidator = mockk<MemberFriendValidator>()
    val todoReader = mockk<TodoReader>()
    val memberFriendReader = mockk<MemberFriendReader>()
    val memberFriendSaver = mockk<MemberFriendSaver>(relaxed = true)
    val memberFriendDeleter = mockk<MemberFriendDeleter>()
    val memberFriendUpdater = mockk<MemberFriendUpdater>()
    val memberProcessor = mockk<MemberProcessor>()
    val memberFriendCreator = mockk<MemberFriendCreator>()
    val fcmProcessor = mockk<FcmProcessor>(relaxed = true)
    val memberFriendService = MemberFriendService(
        memberReader,
        memberFriendValidator,
        todoReader,
        memberFriendReader,
        memberFriendSaver,
        memberFriendDeleter,
        memberFriendUpdater,
        memberProcessor,
        memberFriendCreator,
        fcmProcessor
    )
    val member = createMember(1L, "appleId@naver.com")

    val receiver1 = createMember(3L, "receiver1@example.com")
    val receiver2 = createMember(4L, "receiver2@example.com")
    val sender1 = createMember(5L, "sender1@example.com")
    val sender2 = createMember(6L, "sender2@example.com")

    val memberFriend = createMemberFriend(sender1, member, FriendStatus.FOLLOWING)

    val expectedFollowingSendFriends = listOf(
        createMemberFriend(member, receiver1, FriendStatus.FOLLOWING),
        createMemberFriend(member, receiver2, FriendStatus.NOT_FRIEND),
        createMemberFriend(sender1, member, FriendStatus.FOLLOWING),
    )

    val notFriend =
        expectedFollowingSendFriends.filter { it.friendStatus == FriendStatus.NOT_FRIEND }
    Given("친구 목록을 조회하는 경우") {
        every { memberReader.getMember(any()) } returns member
        every {
            memberFriendReader.findAllFriends(
                member,
                FriendStatus.FOLLOWING
            )
        } returns expectedFollowingSendFriends
        When("친구가 존재한다면") {
            val result =
                memberFriendService.findMemberFriendList("appleId@naver.com")
            Then("친구 목록이 반환되어야한다.") {
                result shouldHaveSize expectedFollowingSendFriends.size
            }
            Then("반환된 친구 목록은 MemberFriendResponse에 매핑되어야 한다.") {
                result.map { it.memberId }.toSet() shouldBe
                        expectedFollowingSendFriends.map { it.getFriendOf(member).memberId }.toSet()
            }
        }
    }

    Given("친구 추가 요청을 할 때") {
        every { memberReader.getMember(any()) } returns member
        every { memberReader.getFriendMember(any()) } returns sender1
        every { memberFriendValidator.isFriend(member, sender1) } returns Unit
        every { memberFriendCreator.create(any(), any(), any()) } returns memberFriend
        memberFriendSaver.save(memberFriend)
        fcmProcessor.pushNotification(
            FcmRequest(
                sender1.clientToken!!,
                sender1.gitHubUsername!!
            ),
            NotificationType.FRIEND_REQUEST
        )
        When("registerFriend()를 호출하면") {
            memberFriendService.registerFriend(sender1.memberId!!, member.appleId!!)
            Then("친구 등록을 위한 유효성 검사가 실행되어야 한다.") {
                verify { memberFriendValidator.isFriend(member, sender1) }
            }

            Then("MemberFriend가 생성 및 저장되어야 한다.") {
                verify { memberFriendCreator.create(any(), member, sender1) }
                verify { memberFriendSaver.save(memberFriend) }
            }

            Then("친구에게 FCM 알림이 전송되어야 한다.") {
                verify {
                    fcmProcessor.pushNotification(
                        FcmRequest(sender1.clientToken!!, member.gitHubUsername!!),
                        NotificationType.FRIEND_REQUEST
                    )
                }
            }
        }
    }

    Given("친구를 언팔하는 경우") {
        every { memberReader.getMember(any()) } returns member
        every { memberReader.getFriendMember(any()) } returns sender1
        every { memberFriendReader.findFriend(any(), any(), any()) } returns memberFriend
        every { memberFriendDeleter.delete(memberFriend) } returns Unit
        When("deleteFriend()를 호출하면") {
            memberFriendService.deleteFriend(sender1.memberId!!, member.appleId!!, "FOLLOWING")
            Then("MemberFriend 엔티티가 정상적으로 삭제되어야 한다")
            verify { memberFriendDeleter.delete(memberFriend) }
        }
    }

    Given("나에게 온 친구 요청 목록을 조회할 때") {
        every { memberReader.getMember(eq(member.appleId!!)) } returns member
        every {
            memberFriendReader.findReceiverMemberList(
                member,
                FriendStatus.NOT_FRIEND
            )
        } returns notFriend
        When("findWaitFriends()를 호출하면") {
            val result = memberFriendService.findWaitFriends(member.appleId!!)
            Then("나에게 온 친구 요청 목록이 조회되어야한다.") {
                result shouldHaveSize notFriend.size
            }
            Then("반환된 친구 목록은 MemberFriendResponse에 매핑되어야 한다.") {
                result.map { it.memberId }.toSet() shouldBe
                        notFriend.map { it.senderMember.memberId }.toSet()
            }
        }
    }

    Given("친구 요청을 수락할 때") {
        every { memberReader.getMember(any()) } returns member
        every { memberReader.getFriendMember(any()) } returns sender1
        every {
            memberFriendReader.findSenderMemberAndReceiverMember(any(), any())
        } returns memberFriend
        every { memberFriendUpdater.updateStatus(memberFriend) } returns Unit
        fcmProcessor.pushNotification(
            FcmRequest(
                sender1.clientToken!!,
                member.gitHubUsername!!
            ),
            NotificationType.FRIEND_REQUEST
        )
        When("approveRequest()을 호출하면") {
            memberFriendService.approveRequest(sender1.memberId!!, member.appleId!!)

            Then("상태 업데이트 메서드가 호출되어야 한다.") {
                verify { memberFriendUpdater.updateStatus(memberFriend) }
            }

            Then("친구 상태가 업데이트 되어야한다.") {
                memberFriend.friendStatus shouldBe FriendStatus.FOLLOWING
            }
            Then("친구에게 FCM 알림이 전송되어야 한다.") {
                verify {
                    fcmProcessor.pushNotification(
                        FcmRequest(sender1.clientToken!!, member.gitHubUsername!!),
                        NotificationType.FRIEND_REQUEST
                    )
                }
            }
        }
    }

    Given("내가 보낸 친구 요청 목록을 조회할 때") {
        every { memberReader.getMember(any()) } returns member
        every {
            memberFriendReader.findSenderMemberList(
                member,
                FriendStatus.NOT_FRIEND
            )
        } returns notFriend
        When("findSendRequestList()를 호출하면") {
            val result = memberFriendService.findSendRequestList(member.appleId!!)
            Then("내가 요청 보낸 친구 목록이 조회되어야한다.") {
                result shouldHaveSize notFriend.size
            }
            Then("반환된 친구 목록은 MemberFriendResponse에 매핑되어야 한다.") {
                result.map { it.memberId }.toSet() shouldBe
                        notFriend.map { it.receiverMember.memberId }.toSet()
            }
        }
    }

    Given("친구의 할 일 목록을 조회할 때") {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val todos = listOf(
            createDeadlineTodoResponse(
                deadline = today,
                todos = listOf(
                    createTodoResponse(1L, "알고리즘 풀기", today),
                    createTodoResponse(2L, "블로그 정리", today)
                )
            ),
            createDeadlineTodoResponse(
                deadline = tomorrow,
                todos = listOf(
                    createTodoResponse(3L, "운동하기", tomorrow),
                    createTodoResponse(4L, "독서하기", tomorrow, TodoStatus.DONE)
                )
            )
        )

        every { memberReader.getMember(member.appleId!!) } returns member
        every { memberReader.getFriendMember(sender1.memberId!!) } returns sender1
        every {
            memberFriendValidator.isAlreadyFriend(member, sender1, FriendStatus.FOLLOWING)
        } returns true
        every { todoReader.bringTodoListForWeek(LocalDate.now(), sender1) } returns todos
        When("searchFriendTodo()를 호출하면") {
            val result =
                memberFriendService.searchFriendTodo(sender1.memberId!!, member.appleId!!)
            Then("친구의 할 일 목록이 반환되어야한다.") {
                result shouldHaveSize todos.size
            }
        }
    }

    Given("친구가 아닌 사람의 투두를 조회하려 할 때") {
        every { memberReader.getMember(eq(member.appleId!!)) } returns member
        every { memberReader.getFriendMember(any()) } returns sender1
        every {
            memberFriendValidator.isAlreadyFriend(member, sender1, FriendStatus.FOLLOWING)
        } returns false

        When("searchFriendTodo를 호출하면") {
            Then("NOT_FRIENDSHIP 예외가 발생해야 한다.") {
                throwsWith<ToDeveloperDoException>(
                    {
                        memberFriendService.searchFriendTodo(sender1.memberId!!, member.appleId!!)
                    },
                    { ex -> ex.errorCode shouldBe ErrorCode.NOT_FRIENDSHIP }
                )
            }
        }
    }

    Given("깃허브 이름으로 사용자를 검색하는 경우") {
        val memberResponses = listOf(
            createMemberResponse(1L, "apple"),
            createMemberResponse(2L, "jun", friendStatus = FriendStatus.REQUEST),
            createMemberResponse(3L, "seok", friendStatus = FriendStatus.RECEIVE),
            createMemberResponse(4L, "lee", friendStatus = FriendStatus.NOT_FRIEND)
        )

        every { memberProcessor.findMemberList(any()) } returns memberResponses
        And("검색한 깃 이름의 사용자가 존재하는 경우") {
            val result = memberFriendService.getGitFriend("jun", member.appleId!!)

            Then("해당 사용자가 반환되어야 한다.") {
                result.username shouldBe "jun"
                result.friendStatus shouldBe FriendStatus.REQUEST
            }
        }

        And("검색한 깃 이름의 사용자가 존재하지 않는 경우") {
            Then("NOT_EXIST_MEMBER 에러가 발생해야 한다.") {
                throwsWith<ToDeveloperDoException>(
                    { memberFriendService.getGitFriend("juns", member.appleId!!) },
                    { ex -> ex.errorCode shouldBe ErrorCode.NOT_EXIST_MEMBER }
                )
            }
        }
    }

    Given("사용자와 친구인 유저를 조회할 때") {
        every { memberReader.getFriendMember(1L) } returns member
        val friendResponse = member.toMemberFriendResponse()
        When("findMemberFriend()를 호출하면") {
            val result = memberFriendService.findMemberFriend(1L)
            Then("정상적으로 친구가 반환되어야 한다.") {
                result shouldBe friendResponse
            }
        }
    }
})

fun createTodoResponse(
    id: Long,
    content: String,
    deadline: LocalDate,
    status: TodoStatus = TodoStatus.PROCEED,
) = TodoResponse(
    todoListId = id,
    content = content,
    memo = "",
    tag = "",
    deadline = deadline,
    todoStatus = status
)

fun createDeadlineTodoResponse(
    deadline: LocalDate,
    todos: List<TodoResponse>,
) = DeadlineTodoResponse(
    deadline = deadline,
    todoResponse = todos
)

fun createMemberResponse(
    memberId: Long,
    username: String,
    avatarUrl: String = "avatarUrl",
    gitUrl: String = "gitUrl/$username",
    friendStatus: FriendStatus = FriendStatus.FOLLOWING,
): MemberResponse = MemberResponse(
    memberId = memberId,
    username = username,
    avatarUrl = avatarUrl,
    gitUrl = gitUrl,
    friendStatus = friendStatus
)

