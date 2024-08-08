package io.junseok.todeveloperdo.presentation.memberfriend

import io.junseok.todeveloperdo.domains.memberfriend.service.MemberFriendService
import io.junseok.todeveloperdo.presentation.memberfriend.dto.request.FriendNameRequest
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/member-friend")
class MemberFriendController(
    private val memberFriendService: MemberFriendService
) {

    /**
     * NOTE
     * 친구 목록 조회(FOLLOW 만)
     */
    @GetMapping
    fun showMemberFriends(principal: Principal): ResponseEntity<List<MemberFriendResponse>> =
        ResponseEntity.ok(memberFriendService.findMemberFriendList(principal.name))

    /**
     * NOTE
     * 나에게 온 친구 요청 목록 조회
     */
    @GetMapping("/request-list")
    fun showWaitFriend(principal: Principal): ResponseEntity<List<MemberFriendResponse>> =
        ResponseEntity.ok(memberFriendService.findWaitFriends(principal.name))

    /**
     * NOTE
     * 단일 친구 조회
     */
    @GetMapping("/{memberId}")
    fun searchMemberFriend(
        @PathVariable memberId: Long, principal: Principal
    ): ResponseEntity<MemberFriendResponse> =
        ResponseEntity.ok(memberFriendService.findMemberFriend(principal.name, memberId))

    /**
     * NOTE
     * 친구 추가 요청
     */
    @GetMapping("/add/{friendId}")
    fun addFriend(@PathVariable friendId: Long, principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberFriendService.registerFriend(friendId, principal.name))

    /**
     * NOTE
     * 친구 삭제(언팔)
     */
    @DeleteMapping("/{friendId}")
    fun unFollowFriend(@PathVariable friendId: Long, principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberFriendService.deleteFriend(friendId, principal.name))

    /**
     * NOTE
     * 친구 요청 수락
     */
    @GetMapping("/accept/{friendId}")
    fun acceptFriendRequest(
        @PathVariable friendId: Long, principal: Principal
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(memberFriendService.approveRequest(friendId, principal.name))

    /**
     * NOTE
     * 내가 보낸 요청 목록 조회
     */
    @GetMapping("/send-list")
    fun showSendFriends(principal: Principal): ResponseEntity<List<MemberFriendResponse>> =
        ResponseEntity.ok(memberFriendService.findSendRequestList(principal.name))

    /**
     * NOTE
     * 친구인 다른 사람 할 일 목록 보기
     */
    @GetMapping("/lookup/todolist/{friendId}")
    fun lookUpFriendTodo(
        @PathVariable friendId: Long, principal: Principal
    ): ResponseEntity<List<TodoResponse>> =
        ResponseEntity.ok(memberFriendService.searchFriendTodo(friendId, principal.name))

    /**
     * NOTE
     * 친구 깃허브 이름으로 친구 검색
     */
    @PostMapping("/search")
    fun searchFriend(@RequestBody friendNameRequest: FriendNameRequest)
    : ResponseEntity<MemberFriendResponse> =
        ResponseEntity.ok(memberFriendService.getFriend(friendNameRequest.gitUserName))

}
