package io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.repository.MemberFriendRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component

@Component
class MemberFriendValidator(private val memberFriendRepository: MemberFriendRepository) {
    // 내가 요청을 보냈으면 true, 아니면 false
    fun checkMember(memberFriend: MemberFriend, member: Member): Boolean {
        return memberFriend.senderMember.memberId == member.memberId
    }

    // 통합검사 (회원가입할 떄)
    fun isFriend(member: Member, friend: Member) {
        if(isAlreadyFriend(member, friend, FriendStatus.FOLLOWING)){
            throw ToDeveloperDoException{ErrorCode.ALREADY_FRIENDSHIP}
        }
        isSendRequestFriend(member, friend, FriendStatus.NOT_FRIEND)
        isRequestedFriend(member, friend, FriendStatus.NOT_FRIEND)
    }


    // 친구관계인지 확인
    fun isAlreadyFriend(member: Member, friend: Member, friendStatus: FriendStatus): Boolean {
        return (memberFriendRepository.isSendFriend(member, friend, friendStatus)
                || memberFriendRepository.isRequestFriend(member, friend, friendStatus))
    }

    // 이미 친구 요청을 보낸 경우
    fun isSendRequestFriend(member: Member, friend: Member, friendStatus: FriendStatus) {
        if (memberFriendRepository.isSendFriend(member, friend, friendStatus)) {
            throw ToDeveloperDoException { ErrorCode.ALREADY_SEND_FRIEND_REQUEST }
        }
    }

    //친구 요청을 받은 경우
    fun isRequestedFriend(member: Member, friend: Member, friendStatus: FriendStatus) {
        if (memberFriendRepository.isRequestFriend(member, friend, friendStatus)) {
            throw ToDeveloperDoException { ErrorCode.ALREADY_REQUESTED_FRIEND }
        }
    }
}