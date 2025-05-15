package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.ints.exactly
import io.kotest.matchers.shouldBe
import io.mockk.*

class MemberFriendDeleterTest : BehaviorSpec({
    val memberFriendRepository = mockk<MemberFriendRepository>()
    val memberFriendDeleter = MemberFriendDeleter(memberFriendRepository)

    Given("친구삭제를 하는 경우") {
        val sender = createMember(1, "apple1")
        val receiver = createMember(2, "apple2")
        val memberFriend = createMemberFriend(sender, receiver, FriendStatus.NOT_FRIEND)
        every { memberFriendRepository.delete(memberFriend) } just runs
        When("delete()를 호출하면") {
            memberFriendDeleter.delete(memberFriend)
            Then("친구가 정상적으로 삭제가 되어야한다.") {
                verify(exactly = 1) { memberFriendRepository.delete(memberFriend) }
            }
        }
    }
})
