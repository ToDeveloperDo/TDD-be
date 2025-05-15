package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFriendUpdater {
    @Transactional
    fun updateStatus(memberFriend: MemberFriend) = memberFriend.updateFriendStatus()
}