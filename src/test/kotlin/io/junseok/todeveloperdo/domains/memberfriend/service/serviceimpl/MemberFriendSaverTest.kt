package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class MemberFriendSaverTest : BehaviorSpec({
    val memberFriendRepository = mockk<MemberFriendRepository>()
    val memberFriendSaver = MemberFriendSaver(memberFriendRepository)

    Given("친구 추가할 때"){
        val sender = createMember(1, "apple1")
        val receiver = createMember(2, "apple2")
        val memberFriend = createMemberFriend(sender, receiver, FriendStatus.NOT_FRIEND)
        every { memberFriendRepository.save(memberFriend) } returns memberFriend
        When("save()를 호출하면"){
            val result = memberFriendSaver.save(memberFriend)
            Then("DB에 정상적으로 저장이 되어야한다."){
                verify(exactly = 1) { memberFriendRepository.save(memberFriend) }
                result shouldBe memberFriend
            }
        }
    }
})
