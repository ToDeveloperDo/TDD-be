package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.exception.ErrorCode.*
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class MemberFriendReaderTest : BehaviorSpec({
    val memberFriendRepository = mockk<MemberFriendRepository>()
    val memberFriendReader = MemberFriendReader(memberFriendRepository)


    val sender = createMember(1L, "sender@example.com")
    val receiver = createMember(2L, "receiver@example.com")

    val receiver1 = createMember(3L, "receiver1@example.com")
    val receiver2 = createMember(4L, "receiver2@example.com")
    val sender1 = createMember(5L, "sender1@example.com")
    val sender2 = createMember(6L, "sender2@example.com")

    val memberFriend = createMemberFriend(sender1, receiver, FriendStatus.REQUEST)

    val expectedFollowingSendFriends = listOf(
        createMemberFriend(sender, receiver1, FriendStatus.FOLLOWING),
        createMemberFriend(sender, receiver2, FriendStatus.NOT_FRIEND)
    )

    val expectedFollowingReceiveFriends = listOf(
        createMemberFriend(sender1, receiver, FriendStatus.FOLLOWING),
        createMemberFriend(sender2, receiver, FriendStatus.NOT_FRIEND)
    )

    fun assertOnlyFollowingFriends(result: List<MemberFriend>, expectedSize: Int) {
        with(result) {
            size shouldBe expectedSize
            all { it.friendStatus == FriendStatus.FOLLOWING } shouldBe true
            none { it.friendStatus != FriendStatus.FOLLOWING } shouldBe true
        }
    }

    Given("내가 보낸 FOLLOWING 상태의 친구 요청이 존재할 때") {
        every {
            memberFriendRepository.findBySenderMemberAndFriendStatus(
                sender,
                FriendStatus.FOLLOWING
            )
        } returns expectedFollowingSendFriends.filter { it.friendStatus == FriendStatus.FOLLOWING }

        When("findSenderMemberList()를 호출하면") {
            val result =
                memberFriendReader.findSenderMemberList(
                    sender,
                    FriendStatus.FOLLOWING
                )

            Then("FOLLOWING 상태인 친구들만 조회된다") {
                assertOnlyFollowingFriends(result, 1)
            }
        }
    }

    Given("내가 받은 FOLLOWING 상태의 친구 요청이 존재할 때") {
        every {
            memberFriendRepository.findByReceiverMemberAndFriendStatus(
                receiver,
                FriendStatus.FOLLOWING
            )
        } returns expectedFollowingReceiveFriends.filter { it.friendStatus == FriendStatus.FOLLOWING }

        When("findReceiverMemberList()를 호출하면") {
            val result =
                memberFriendReader.findReceiverMemberList(
                    receiver,
                    FriendStatus.FOLLOWING
                )

            Then("FOLLOWING 상태인 친구들만 조회된다") {
                assertOnlyFollowingFriends(result, 1)
            }
        }
    }

    Given("친구 요청 여부에 따라") {
        And("친구 요청이 존재하는 경우") {
            every {
                memberFriendRepository.findBySenderMemberAndReceiverMember(sender, receiver)
            } returns memberFriend
            When("내가 친구요청 보낸 친구가 존재한다면") {
                val result =
                    memberFriendReader.findSenderMemberAndReceiverMember(sender, receiver)
                Then("요청 보낸 친구들만 조회된다.") {
                    with(result) {
                        receiverMember shouldBe receiver
                        senderMember shouldBe sender1
                        friendStatus shouldBe FriendStatus.REQUEST
                    }
                }
            }
        }
        And("친구 요청이 존재하지 않는 경우") {
            every {
                memberFriendRepository.findBySenderMemberAndReceiverMember(sender, receiver)
            } throws ToDeveloperDoException { NOT_REQUEST_FRIEND }
            When("내가 친구요청 보낸 친구가 존재하지 않을 때") {
                Then("요청 보낸 친구가 아닌 경우는 NOT_REQUEST_FRIEND가 발생해야한다.") {
                    val exception = shouldThrow<ToDeveloperDoException> {
                        memberFriendReader.findSenderMemberAndReceiverMember(sender, receiver)
                    }
                    exception.errorCode shouldBe NOT_REQUEST_FRIEND
                }
            }
        }
    }

    Given("나와 친구인 사용자가 존재하는 경우") {
        every {
            memberFriendRepository.findAllByFriend(
                sender,
                FriendStatus.FOLLOWING
            )
        } returns expectedFollowingSendFriends.filter { it.friendStatus == FriendStatus.FOLLOWING }
        When("친구인 사용자를 조회할 때") {
            val result = memberFriendReader.findAllWithFriend(sender)
            Then("친구인 사용자만 조회가 되어야한다.") {
                assertOnlyFollowingFriends(result, 1)
            }
        }
    }

    Given("친구를 언팔하고 싶을 때") {
        And("친구관계인 경우") {
            every {
                memberFriendRepository.findByFriendRelationship(
                    any(),
                    any(),
                    FriendStatus.FOLLOWING
                )
            } returns memberFriend

            When("findFriend()를 호출하면") {
                val result =
                    memberFriendReader.findFriend(receiver, sender1, FriendStatus.FOLLOWING)
                Then("친구삭제가 되어야한다.") {
                    result.receiverMember.memberId shouldBe receiver.memberId
                    result.senderMember.memberId shouldBe sender1.memberId
                }
            }
        }
        And("친구 관계가 아닌 경우"){
            every {
                memberFriendRepository.findByFriendRelationship(
                    any(),
                    any(),
                    FriendStatus.FOLLOWING
                )
            } throws ToDeveloperDoException { NOT_FRIENDSHIP }
            When("findFriend()를 호출하면"){
                Then("NOT_FRIENDSHIP 에러가 발생한다."){
                    val exception = shouldThrow<ToDeveloperDoException> {
                        memberFriendReader.findFriend(
                            receiver,
                            sender1,
                            FriendStatus.FOLLOWING
                        )
                    }
                    exception.errorCode shouldBe NOT_FRIENDSHIP
                }
            }
        }
    }

})

fun createMemberFriend(sender: Member, receiver: Member, friendStatus: FriendStatus): MemberFriend =
    MemberFriend(
        memberFriendId = MemberFriendId(sender.memberId!!, receiver.memberId!!),
        friendStatus = friendStatus,
        senderMember = sender,
        receiverMember = receiver
    )
