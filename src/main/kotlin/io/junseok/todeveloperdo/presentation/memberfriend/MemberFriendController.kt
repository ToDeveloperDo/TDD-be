package io.junseok.todeveloperdo.presentation.memberfriend

import io.junseok.todeveloperdo.domains.memberfriend.service.MemberFriendService
import io.junseok.todeveloperdo.presentation.memberfriend.dto.response.MemberFriendResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/member-friend")
class MemberFriendController(
    private val memberFriendService: MemberFriendService
) {

    /**단일
     * NOTE
     * 친구 목록 조회
     */
    @GetMapping
    fun showMemberFriends(principal: Principal): ResponseEntity<List<MemberFriendResponse>> =
        ResponseEntity.ok(memberFriendService.findMemberFriendList(principal.name))

    /**
     * NOTE
     * 단일 친구 조회
     */
    @GetMapping("/{memberId}")
    fun searchMemberFriend(
        @PathVariable memberId: Long,
        principal: Principal
    ): ResponseEntity<MemberFriendResponse> =
        ResponseEntity.ok(memberFriendService.findMemberFriend(principal.name,memberId))

    /**
     * NOTE
     * 친구 추가
     */

    /**
     * NOTE
     * 서비스에 등록된 멤버 전부 조회
     */

    /**
     * NOTE
     * 친구 삭제(언팔)
     */
}
