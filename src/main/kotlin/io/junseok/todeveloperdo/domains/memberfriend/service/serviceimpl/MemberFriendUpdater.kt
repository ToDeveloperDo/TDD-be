package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import org.springframework.stereotype.Component

@Component
class MemberFriendUpdater {
    fun updateStatus(memberFriend: MemberFriend) = memberFriend.updateFriendStatus()
}