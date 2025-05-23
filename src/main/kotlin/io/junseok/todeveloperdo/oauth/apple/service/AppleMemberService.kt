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

    fun createOrUpdateMember(
        appleId: String,
        email: String,
        refreshToken: String,
        clientToken: String
    ) {
        if (!memberValidator.isExistMember(appleId)) {
            val member =
                memberCreator.generatorAppleMember(appleId, email, refreshToken, clientToken)
            memberSaver.saveMember(member)
        } else {
            val member = memberReader.getMember(appleId)
            member.appleEmail = email
            member.clientToken = clientToken
        }
    }

    fun revoke(appleId: String) {
        val member = memberReader.getMember(appleId)
        val clientSecret = clientSecretCreator.createClientSecret()

        try {
            appleWithdrawClient.revokeToken(
                clientId = clientId,
                clientSecret = clientSecret,
                token = member.appleRefreshToken!!,
                tokenTypeHint = "refresh_token"
            )
            memberDeleter.removeMember(appleId)
        } catch (e: Exception) {
            throw e
        }
    }
}
