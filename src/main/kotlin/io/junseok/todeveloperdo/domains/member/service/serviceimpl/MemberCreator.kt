package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Authority
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import org.springframework.stereotype.Component

@Component
class MemberCreator() {
    fun generatorAppleMember(
        appleId: String,
        email: String,
        refreshToken: String,
        clientToken: String
    ) = Member(
        appleId = appleId,
        appleEmail = email,
        appleRefreshToken = refreshToken,
        clientToken = clientToken,
        authority = Authority.ROLE_USER
    )
}