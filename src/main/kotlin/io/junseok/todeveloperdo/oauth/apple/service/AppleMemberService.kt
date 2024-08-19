package io.junseok.todeveloperdo.oauth.apple.service

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.*
import io.junseok.todeveloperdo.oauth.apple.client.AppleWithdrawClient
import io.junseok.todeveloperdo.oauth.apple.service.serviceimpl.ClientSecretCreator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors




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
    private val logger = LoggerFactory.getLogger(AppleMemberService::class.java)

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
        val uriStr = "https://appleid.apple.com/auth/revoke"
        val clientSecret = clientSecretCreator.createClientSecret()
        val params: MutableMap<String, String> = HashMap()
        params["client_secret"] = clientSecret // 생성한 client_secret
        params["token"] = member.appleRefreshToken!! // 생성한 refresh_token
        params["client_id"] = clientId // app bundle id

        try {
            val getRequest: HttpRequest = HttpRequest.newBuilder()
                .uri(URI(uriStr))
                .POST(getParamsUrlEncoded(params))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .build()

            val httpClient: HttpClient = HttpClient.newHttpClient()
            httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString())
            memberDeleter.removeMember(appleId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*val
        try {
            appleWithdrawClient.revokeToken(clientId, clientSecret, member.appleRefreshToken!!)
            memberDeleter.removeMember(appleId)
        } catch (e: Exception) {
            // 실패 시 로그 기록이나 예외 처리 로직 추가
            logger.error("failed: ",e)
        }*/
    }

    private fun getParamsUrlEncoded(parameters: Map<String, String>): BodyPublisher {
        val urlEncoded = parameters.entries
            .stream()
            .map<String> { e: Map.Entry<String, String> ->
                e.key + "=" + URLEncoder.encode(
                    e.value,
                    StandardCharsets.UTF_8
                )
            }
            .collect(Collectors.joining("&"))
        return HttpRequest.BodyPublishers.ofString(urlEncoded)
    }
}
