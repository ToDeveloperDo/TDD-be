package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendReader
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk

class MemberProcessorTest : BehaviorSpec({
    val memberReader = mockk<MemberReader>()
    val memberFriendReader = mockk<MemberFriendReader>()
    val memberProcessor = MemberProcessor(memberReader, memberFriendReader)
    Given("서비스에 등록된 모든 멤버들을 조회할 때") {
        val appleId: String = "appleId"
        val member = createMember(1L, "appleId")
        every { memberReader.getMember(appleId) } returns member
        val otherMembers = listOf(
            createMember(2L, "test1"),
            createMember(3L, "test2"),
            createMember(4L, "test3"),
            createMember(5L, "test5"),
            createMember(6L, "test6")
        )
        every { memberReader.getMembersExcludeMe(member) } returns otherMembers

        // 받은 친구 요청 목록 (RECEIVE)
        every { memberFriendReader.receiverMemberByFriendStatus(member) } returns listOf(
            createMemberFriend(otherMembers[3], member, FriendStatus.RECEIVE)
        )
        // 보낸 친구 요청 목록 (REQUEST)
        every { memberFriendReader.senderMemberByFriendStatus(member) } returns listOf(
            createMemberFriend(member, otherMembers[2], FriendStatus.REQUEST)
        )
        // 친구 목록 (FOLLOWING)
        every { memberFriendReader.findAllWithFriend(member) } returns listOf(
            createMemberFriend(otherMembers[4], member, FriendStatus.FOLLOWING)
        )
        When("나를 제외한 친구를 조회하고 친구 상태에 따라 분리할 때") {
            val findMemberList = memberProcessor.findMemberList(appleId)
            Then("친구 상태가 정상적으로 분류되어야 한다") {
                findMemberList.map { it.friendStatus } shouldContainExactly listOf(
                    FriendStatus.NOT_FRIEND,
                    FriendStatus.NOT_FRIEND,
                    FriendStatus.REQUEST,
                    FriendStatus.RECEIVE,
                    FriendStatus.FOLLOWING
                )
            }

            Then("친구 목록에서 나를 제외한 멤버들만 반환되어야 한다") {
                findMemberList.map { it.memberId } shouldContainExactly listOf(2L, 3L, 4L, 5L, 6L)
            }
        }
    }
})

fun createMemberFriend(sender: Member, receiver: Member, status: FriendStatus) = MemberFriend(
    memberFriendId = MemberFriendId(
        senderMemberId = sender.memberId!!,
        receiverMemberId = receiver.memberId!!
    ),
    friendStatus = status,
    senderMember = sender,
    receiverMember = receiver
)