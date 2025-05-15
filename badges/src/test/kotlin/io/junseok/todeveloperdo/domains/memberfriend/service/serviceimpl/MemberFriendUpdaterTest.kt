package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberFriendUpdaterTest : BehaviorSpec({
    val memberFriendUpdater = MemberFriendUpdater()
    val sender = createMember(1L, "sender@example.com")
    val receiver = createMember(2L, "receiver@example.com")
    Given("친구 상태를 업데이트 할 때") {
        val memberFriend = createMemberFriend(sender, receiver, FriendStatus.REQUEST)

        When("친구가 되는 경우") {
            memberFriendUpdater.updateStatus(memberFriend)
            Then("친구상태로 변경되어야한다.") {
                memberFriend.friendStatus shouldBe FriendStatus.FOLLOWING
            }
        }
    }

})
