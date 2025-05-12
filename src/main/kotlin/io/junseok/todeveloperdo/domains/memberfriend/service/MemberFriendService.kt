package io.junseok.todeveloperdo.domains.memberfriend.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberProcessor
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.FriendStatus
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.MemberFriendId
import io.junseok.todeveloperdo.domains.memberfriend.persistence.entity.getFriendOf
import io.junseok.todeveloperdo.domains.memberfriend.service.serviceimpl.*
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.global.fcm.FcmProcessor
import io.junseok.todeveloperdo.global.fcm.dto.request.FcmRequest
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.toMemberFriendResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.DeadlineTodoResponse
import io.junseok.todeveloperdo.scheduler.fcm.NotificationType
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
    private val memberFriendUpdater: MemberFriendUpdater,
    private val memberProcessor: MemberProcessor,
    private val memberFriendCreator: MemberFriendCreator,
    private val fcmProcessor: FcmProcessor,
) {
    fun findMemberFriendList(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.findAllFriends(member, FriendStatus.FOLLOWING)
            .map { friend -> friend.getFriendOf(member).toMemberFriendResponse() }
    }

    fun findMemberFriend(memberId: Long): MemberFriendResponse {
        val member = memberReader.getFriendMember(memberId)
        return member.toMemberFriendResponse()
    }

    fun registerFriend(friendId: Long, username: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        memberFriendValidator.isFriend(member, friendMember)
        val memberFriendId = MemberFriendId(member.memberId!!, friendId)
        val memberFriend =
            memberFriendCreator.create(memberFriendId, member, friendMember)
        memberFriendSaver.save(memberFriend)
        fcmProcessor.pushNotification(
            FcmRequest(friendMember.clientToken!!, member.gitHubUsername!!),
            NotificationType.FRIEND_REQUEST
        )
    }

    fun deleteFriend(friendId: Long, username: String, type: String) {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        val findFriend = memberFriendReader.findFriend(
            member,
            friendMember,
            FriendStatus.valueOf(type)

        )
        memberFriendDeleter.delete(findFriend)
    }

    fun findWaitFriends(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.findReceiverMemberList(member, FriendStatus.NOT_FRIEND)
            .map { it.senderMember.toMemberFriendResponse() }
    }
    // TODO
    fun approveRequest(friendId: Long, username: String) {
        val member = memberReader.getMember(username) //나
        val friendMember = memberReader.getFriendMember(friendId) //친구 요청을 보낸 사람
        val memberFriend =
            memberFriendReader.findSenderMemberAndReceiverMember(friendMember, member)
        memberFriendUpdater.updateStatus(memberFriend)
        fcmProcessor.pushNotification(
            FcmRequest(friendMember.clientToken!!, member.gitHubUsername!!),
            NotificationType.FRIEND_REQUEST_ACCEPTED
        )
    }

    fun findSendRequestList(username: String): List<MemberFriendResponse> {
        val member = memberReader.getMember(username)
        return memberFriendReader.findSenderMemberList(member, FriendStatus.NOT_FRIEND)
            .map { it.receiverMember.toMemberFriendResponse() }
    }

    fun searchFriendTodo(friendId: Long, username: String): List<DeadlineTodoResponse> {
        val member = memberReader.getMember(username)
        val friendMember = memberReader.getFriendMember(friendId)
        if (!memberFriendValidator.isAlreadyFriend(member, friendMember, FriendStatus.FOLLOWING)) {
            throw ToDeveloperDoException { ErrorCode.NOT_FRIENDSHIP }
        }
        return todoReader.bringTodoListForWeek(LocalDate.now(), friendMember)
    }

    fun getGitFriend(gitUserName: String, appleId: String): MemberResponse =
        memberProcessor.findMemberList(appleId)
            .find { it.username == gitUserName }
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER }

}
