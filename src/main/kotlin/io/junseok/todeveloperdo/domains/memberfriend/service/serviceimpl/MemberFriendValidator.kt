package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import org.springframework.stereotype.Component

@Component
class MemberFriendValidator {
    // 내가 요청을 보냈으면 true, 아니면 false
    fun checkMember(memberFriend: MemberFriend,member: Member): Boolean{
        return memberFriend.senderMember.memberId == member.memberId
    }
}