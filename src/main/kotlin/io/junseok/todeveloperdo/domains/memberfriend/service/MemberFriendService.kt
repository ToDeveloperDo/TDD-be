package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberFriendService(
    private val memberFriendRepository: MemberFriendRepository,
    private val memberRepository: MemberRepository
) {
    @Transactional(readOnly = true)
    fun findMemberFriendList(username: String): List<MemberFriendResponse> {
        val member = (memberRepository.findByUsername(username)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER })
        return memberFriendRepository.findByMember(member)
            .stream()
            .map { it.toMemberFriendResponse() }
            .toList()
    }

    @Transactional(readOnly = true)
    fun findMemberFriend(username: String, memberId: Long) =
        (memberFriendRepository.findByIdOrNull(memberId)?.toMemberFriendResponse()
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER })
}