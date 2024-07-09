package io.junseok.todeveloperdo.domains.memberfriend.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import org.springframework.data.jpa.repository.JpaRepository

interface MemberFriendRepository : JpaRepository<MemberFriend,Long> {
    fun findByMember(member: Member): List<MemberFriend>
}