package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MemberFriendValidatorTest : BehaviorSpec({
    val memberFriendRepository = mockk<MemberFriendRepository>()
    val memberFriendValidator = MemberFriendValidator(memberFriendRepository)
    val sender = createMember(1L, "sender@example.com")
    val receiver = createMember(2L, "receiver@example.com")

    fun stubIsFriend(send: Boolean, request: Boolean) {
        every {
            memberFriendRepository.isSendFriend(sender, receiver, FriendStatus.FOLLOWING)
        } returns send

        every {
            memberFriendRepository.isRequestFriend(sender, receiver, FriendStatus.FOLLOWING)
        } returns request
    }

    fun stubAllFalse() {
        every { memberFriendRepository.isSendFriend(any(), any(), any()) } returns false
        every { memberFriendRepository.isRequestFriend(any(), any(), any()) } returns false
    }

    Given("새로운 친구를 등록할 때") {
        listOf(
            Triple(true, true, true),
            Triple(false, false, false)
        ).forEach { (send, request, expected) ->

            And("isSendFriend: $send, isRequestFriend: $request") {
                stubIsFriend(send, request)

                When("isAlreadyFriend()를 호출하면") {
                    val result = memberFriendValidator.isAlreadyFriend(
                        sender, receiver, FriendStatus.FOLLOWING
                    )

                    Then("결과는 $expected 이어야 한다") {
                        result shouldBe expected
                    }
                }
            }
        }
    }

    Given("친구 요청을 보낼 때") {
        And("내가 이미 친구 요청을 보낸 상태라면") {
            every {
                memberFriendRepository.isSendFriend(
                    sender,
                    receiver,
                    FriendStatus.NOT_FRIEND
                )
            } returns true
            When("isSendRequestFriend()를 호출하면") {
                Then("ALREADY_SEND_FRIEND_REQUEST 예외가 발생해야 한다") {
                    throwsWith<ToDeveloperDoException>(
                        {
                            memberFriendValidator.isSendRequestFriend(
                                sender,
                                receiver,
                                FriendStatus.NOT_FRIEND
                            )
                        },
                        { ex -> ex.errorCode shouldBe ErrorCode.ALREADY_SEND_FRIEND_REQUEST }
                    )
                }
            }
        }
        And("상대방이 나에게 친구 요청을 이미 보낸 상태라면") {
            every {
                memberFriendRepository.isRequestFriend(
                    sender,
                    receiver,
                    FriendStatus.NOT_FRIEND
                )
            } returns true
            When("isRequestedFriend()를 호출하면") {
                Then("ALREADY_REQUESTED_FRIEND 예외가 발생해야 한다") {
                    throwsWith<ToDeveloperDoException>(
                        {
                            memberFriendValidator.isRequestedFriend(
                                sender,
                                receiver,
                                FriendStatus.NOT_FRIEND
                            )
                        },
                        { ex ->
                            ex.errorCode shouldBe ErrorCode.ALREADY_REQUESTED_FRIEND
                        }
                    )
                }
            }
        }
    }

    Given("회원가입할 때 친구 상태를 통합 검사할 때") {
        And("이미 친구인 경우") {
            every {
                memberFriendRepository.isSendFriend(
                    sender,
                    receiver,
                    FriendStatus.FOLLOWING
                )
            } returns true
            Then("ALREADY_FRIENDSHIP가 발생해야한다.") {
                throwsWith<ToDeveloperDoException>(
                    { memberFriendValidator.isFriend(sender, receiver) },
                    { ex -> ex.errorCode shouldBe ErrorCode.ALREADY_FRIENDSHIP }
                )
            }
        }

        And("친구 요청을 이미 보낸 경우") {
            every {
                memberFriendRepository.isSendFriend(
                    sender,
                    receiver,
                    FriendStatus.NOT_FRIEND
                )
            } returns true
            Then("ALREADY_SEND_FRIEND_REQUEST가 발생해야한다.") {
                throwsWith<ToDeveloperDoException>(
                    {
                        memberFriendValidator.isSendRequestFriend(
                            sender,
                            receiver,
                            FriendStatus.NOT_FRIEND
                        )
                    },
                    { ex -> ex.errorCode shouldBe ErrorCode.ALREADY_SEND_FRIEND_REQUEST }
                )
            }
        }
        And("친구 요청을 이미 받은 경우") {
            every {
                memberFriendRepository.isRequestFriend(
                    sender,
                    receiver,
                    FriendStatus.NOT_FRIEND
                )
            } returns true
            Then("ALREADY_SEND_FRIEND_REQUEST가 발생해야한다.") {
                throwsWith<ToDeveloperDoException>(
                    {
                        memberFriendValidator.isRequestedFriend(
                            sender,
                            receiver,
                            FriendStatus.NOT_FRIEND
                        )
                    },
                    { ex -> ex.errorCode shouldBe ErrorCode.ALREADY_REQUESTED_FRIEND }
                )
            }
        }
        And("친구 상태가 아니라면"){
            stubAllFalse()
            Then("예외 없이 통과되어야 한다."){
                memberFriendValidator.isFriend(sender,receiver)
            }
        }
    }
})
