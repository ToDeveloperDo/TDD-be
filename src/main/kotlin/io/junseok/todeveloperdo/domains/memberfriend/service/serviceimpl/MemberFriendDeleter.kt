package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class MemberFriendDeleter(
    private val memberFriendRepository: MemberFriendRepository
) {
    @Transactional
    fun delete(memberFriend: MemberFriend) = memberFriendRepository.delete(memberFriend)
}