package io.junseok.todeveloperdo.domains.member.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.MemberFriendReader
import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.toMemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.toMemberResponse
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberReader: MemberReader,
    private val memberUpdater: MemberUpdater,
    private val memberValidator: MemberValidator,
    private val memberDeleter: MemberDeleter,
    private val memberFriendReader: MemberFriendReader
) {
    fun createGitMember(
        gitUserResponse: GitUserResponse,
        accessToken: String,
        username: String
    ) {
        val member = memberReader.getMember(username)
        if (!memberValidator.isExistGitMember(gitUserResponse.username)) {
            memberUpdater.updateGitMemberInfo(gitUserResponse, accessToken, member)
        } else {
            memberUpdater.updateMemberToken(accessToken, member)
        }

    }

    fun findMember(username: String): MemberInfoResponse {
        val member = memberReader.getMember(username)
        return member.toMemberInfoResponse()
    }

    fun deleteMember(username: String) {
        memberDeleter.removeMember(username)
    }

    fun findAllMember(appleId: String): List<MemberResponse> {
        val member = memberReader.getMember(appleId)
        //내가 받은 요청 목록
        val map1 = memberFriendReader.receiverMemberByFriendStatus(member)
            .map { it.senderMember.memberId }

        //내가 보낸 친구 목록
        val map2 = memberFriendReader.senderMemberByFriendStatus(member)
            .map { it.receiverMember.memberId }

        //친구인 사람
        val friends = memberFriendReader.findAllWithFriend(member)

        return memberReader.getAllMember().map { friend ->
            val friendStatus = when {
                map1.contains(friend.memberId) -> FriendStatus.RECEIVE
                map2.contains(friend.memberId) -> FriendStatus.REQUEST
                friends.any {
                    it.senderMember == friend || it.receiverMember == friend
                } -> FriendStatus.FOLLOWING
                else -> FriendStatus.NOT_FRIEND
            }
            friend.toMemberResponse(friendStatus)
        }
    }
}