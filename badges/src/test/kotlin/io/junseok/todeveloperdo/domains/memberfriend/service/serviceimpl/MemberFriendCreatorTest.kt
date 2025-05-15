package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberFriendCreatorTest : BehaviorSpec({
    val memberCreator = MemberFriendCreator()
    Given("MemberFriend를 생성할 때") {
        val sender = createMember(1L, "senderAppleId")
        val receiver = createMember(2L, "receiverAppleId")
        val memberFriendId = MemberFriendId(sender.memberId!!, receiver.memberId!!)

        When("create() 함수를 호출하면") {
            val result = memberCreator.create(memberFriendId, sender, receiver)

            Then("정상적으로 값이 채워진 MemberFriend가 반환되어야 한다") {
                result.memberFriendId shouldBe memberFriendId
                result.senderMember shouldBe sender
                result.receiverMember shouldBe receiver
                result.friendStatus shouldBe FriendStatus.NOT_FRIEND
            }
        }
    }

})
