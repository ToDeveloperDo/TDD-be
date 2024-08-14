package io.junseok.todeveloperdo.oauth.apple.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.oauth.apple.client.AppleWithdrawClient
import io.junseok.todeveloperdo.oauth.apple.service.serviceimpl.ClientSecretCreator
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AppleMemberService(
    @Value("\${spring.security.oauth2.client.registration.apple.client-id}")
    private val clientId: String,
    private val clientSecretCreator: ClientSecretCreator,
    private val memberSaver: MemberSaver,
    private val memberReader: MemberReader,
    private val memberValidator: MemberValidator,
    private val memberCreator: MemberCreator,
    private val memberDeleter: MemberDeleter,
    private val appleWithdrawClient: AppleWithdrawClient
) {

    fun createOrUpdateMember(appleId: String, email: String, refreshToken: String) {
        if (!memberValidator.isExistMember(appleId)) {
            val member = memberCreator.generatorAppleMember(appleId, email, refreshToken)
            memberSaver.saveMember(member)
        } else {
            val member = memberReader.getMember(appleId)
            member.appleEmail = email
        }
    }

    fun revoke(appleId: String) {
        val member = memberReader.getMember(appleId)
        val clientSecret = clientSecretCreator.createClientSecret()
        appleWithdrawClient.revokeToken(clientId, clientSecret, member.appleRefreshToken!!)
        memberDeleter.removeMember(appleId)
    }
}
