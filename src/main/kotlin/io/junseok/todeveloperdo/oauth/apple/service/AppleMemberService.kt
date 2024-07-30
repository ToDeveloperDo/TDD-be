package io.junseok.todeveloperdo.oauth.apple.service

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class AppleMemberService(
    private val memberRepository: MemberRepository
) {

    fun createOrUpdateMember(appleId: String, email: String, refreshToken: String): Member {
        val existingMember = memberRepository.findByAppleId(appleId)

        return if (existingMember != null) {
            existingMember.appleEmail = email
            memberRepository.save(existingMember)
        } else {
            val newMember = Member(
                appleId = appleId,
                appleEmail = email,
                appleRefreshToken = refreshToken
            )
            memberRepository.save(newMember)
        }
    }
}
