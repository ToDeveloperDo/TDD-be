package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendReader
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import javax.annotation.meta.When

class MemberProcessorTest : BehaviorSpec({
    val memberReader = mockk<MemberReader>()
    val memberFriendReader = mockk<MemberFriendReader>()
    val memberProcessor = MemberProcessor(memberReader, memberFriendReader)

    Given("서비스에 등록된 모든 멤버들을 조회할 때") {
        val appleId = "appleId"
        val member = createMember(1L, "appleId")

        every { memberReader.getMember(appleId) } returns member

        val notFriend1 = createMember(2L, "test1")
        val request = createMember(3L, "test2")
        val receive = createMember(4L, "test3")
        val notFriend2 = createMember(5L, "test4")
        val following = createMember(6L, "test5")

        val allMembers = listOf(
            notFriend1,
            request,
            receive,
            notFriend2,
            following
        )
        notFriend2.gitHubUsername = null

        every { memberReader.getMembersExcludeMe(member) } returns allMembers

        // 받은 친구 요청 목록 (RECEIVE)
        every {
            memberFriendReader.findReceiverMemberList(member, FriendStatus.NOT_FRIEND)
        } returns listOf(createMemberFriend(receive, member, FriendStatus.RECEIVE))

        // 보낸 친구 요청 목록 (REQUEST)
        every {
            memberFriendReader.findSenderMemberList(member, FriendStatus.NOT_FRIEND)
        } returns listOf(createMemberFriend(member, request, FriendStatus.REQUEST))

        // 친구 목록 (FOLLOWING)
        every {
            memberFriendReader.findAllWithFriend(member)
        } returns listOf(createMemberFriend(following, member, FriendStatus.FOLLOWING))

        When("나를 제외한 멤버들을 조회하고 친구 상태를 확인할 때") {
            val result = memberProcessor.findMemberList(appleId)

            Then("친구 상태가 정확하게 분류되어야 한다") {
                result.map { it.friendStatus } shouldContainExactly listOf(
                    FriendStatus.NOT_FRIEND,
                    FriendStatus.REQUEST,
                    FriendStatus.RECEIVE,
                    FriendStatus.FOLLOWING
                )
            }

            Then("멤버 ID가 정확히 반환되어야 한다") {
                result.map { it.memberId } shouldContainExactly listOf(
                    2L, 3L, 4L, 6L
                )
            }

            Then("사용자 깃허브 닉네임이 존재하지 않으면 제외되어야한다."){
                result.any { it.memberId == 5L } shouldBe false
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