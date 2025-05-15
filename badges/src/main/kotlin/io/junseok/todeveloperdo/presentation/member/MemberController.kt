package io.junseok.todeveloperdo.presentation.member

import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.presentation.member.dto.request.FcmRequest
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberInfoResponse
import io.junseok.todeveloperdo.presentation.member.dto.response.MemberResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/member")
@CrossOrigin
class MemberController(
    private val memberService: MemberService
) {
    /**
     * NOTE
     * 멤버 정보 조회
     */
    @GetMapping
    fun showMemberInfo(principal: Principal): ResponseEntity<MemberInfoResponse> =
        ResponseEntity.ok(memberService.findMember(principal.name))

    /**
     * 멤버 회원 탈퇴
     */
    @DeleteMapping
    fun withdrawMember(principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberService.deleteMember(principal.name))

    /**
     * NOTE
     * 서비스에 등록된 멤버 전부 조회
     */
    @GetMapping("/all")
    fun showAllMember(principal: Principal): ResponseEntity<List<MemberResponse>> =
        ResponseEntity.ok(memberService.findAllMember(principal.name))

    @PostMapping("/fcm")
    fun reIssuance(principal: Principal, @RequestBody fcmRequest: FcmRequest):ResponseEntity<Unit>{
        memberService.reIssued(principal.name,fcmRequest.fcmToken)
        return ResponseEntity.ok().build()
    }
}