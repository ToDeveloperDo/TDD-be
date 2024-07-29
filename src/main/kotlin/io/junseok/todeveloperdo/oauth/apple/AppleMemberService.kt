package io.junseok.todeveloperdo.oauth.apple

import org.springframework.stereotype.Service

@Service
class AppleMemberService(
    private val memberRepository: AppleMemberRepository
) {

    fun createOrUpdateMember(appleId: String, email: String, refreshToken: String): AppleMember {
        val existingMember = memberRepository.findByAppleId(appleId)

        return if (existingMember != null) {
            existingMember.email = email
            memberRepository.save(existingMember)
        } else {
            val newMember = AppleMember(
                appleId = appleId,
                email = email,
                appleRefreshToken = refreshToken
            )
            memberRepository.save(newMember)
        }
    }
}
