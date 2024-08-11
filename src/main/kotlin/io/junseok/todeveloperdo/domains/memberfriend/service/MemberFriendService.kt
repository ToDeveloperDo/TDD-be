package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriend
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.*
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberFriendService(
    private val memberReader: MemberReader,
    private val memberFriendValidator: MemberFriendValidator,
    private val todoReader: TodoReader,
    private val memberFriendReader: MemberFriendReader,
    private val memberFriendSaver: MemberFriendSaver,
    private val memberFriendDeleter: MemberFriendDeleter,
    private val memberFriendUpdater: MemberFriendUpdater

) {
    fun findMemberFriendList(username: String): List<MemberFriendResponse>? {
        val member = memberReader.getMember(username)
        val senderMember = memberFriendReader.findSenderMemberList(member)
        val receiverMember = memberFriendReader.findReceiverMemberList(member)

        val friends = (senderMember + receiverMember).distinct()

        return friends.map { friend ->
            MemberFriendResponse(
                memberId = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.memberId!! //
                } else {
                    friend.senderMember.memberId!!
                },
                friendUsername = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.gitHubUsername!!
                } else {
                    friend.senderMember.gitHubUsername!!
                },
                friendGitUrl = if (memberFriendValidator.checkMember(friend, member)) {
                    friend.receiverMember.gitHubUrl!!
                } else {
                    friend.senderMember.gitHubUrl!!
                }
            )
        }
    }

    fun findMemberFriend(username: String, memberId: Long): MemberFriendResponse {
        val member = memberReader.getFriendMember(memberId)
        return MemberFriendResponse(
            memberId = memberId,
            friendUsername = member.gitHubUsername!!,
            friendGitUrl = member.gitHubUrl!!
        )
    }

    fun registerFriend(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        memberFriendValidator.isFriend(member, friendMember)
        val memberFriendId = MemberFriendId(member.memberId!!, friendId)

        val memberFriend = MemberFriend(
            memberFriendId = memberFriendId,
            senderMember = member,
            receiverMember = friendMember,
            friendStatus = FriendStatus.NOT_FRIEND
        )
        memberFriendSaver.save(memberFriend)
    }

    fun deleteFriend(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        val memberFriend = memberFriendReader.findSenderMemberAndReceiverMember(member,friendMember)
        memberFriendDeleter.delete(memberFriend)
    }

    fun findWaitFriends(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.receiverMemberByFriendStatus(member)
            .map { it.senderMember.toMemberFriendResponse() }
    }

    fun approveRequest(friendId: Long, username: String) {
        val member = memberReader.getMember(username) //나
        val friendMember = memberReader.getFriendMember(friendId) //친구 요청을 보낸 사람
        val memberFriend = memberFriendReader.findSenderMemberAndReceiverMember(friendMember,member)
        memberFriendUpdater.updateStatus(memberFriend)
    }

    fun findSendRequestList(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.senderMemberByFriendStatus(member)
            .map { it.receiverMember.toMemberFriendResponse() }
    }

    fun searchFriendTodo(friendId: Long, username: String): List<TodoResponse> {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        if (!memberFriendValidator.isAlreadyFriend(member, friendMember, FriendStatus.FOLLOWING)) {
            throw ToDeveloperDoException { ErrorCode.NOT_FRIENDSHIP }
        }
        return todoReader.bringTodoListForWeek(LocalDate.now(), friendMember)
    }

    fun getFriend(gitUserName: String): MemberFriendResponse =
        memberReader.getFriendMemberByGit(gitUserName).toMemberFriendResponse()
}