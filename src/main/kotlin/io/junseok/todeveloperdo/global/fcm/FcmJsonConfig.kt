package io.junseok.todeveloperdo.global.fcm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class FcmJsonConfig(
    @Value("\${fcm.project-id}") projectId: String,
    @Value("\${fcm.private-key-id}") privateKeyId: String,
    @Value("\${fcm.private-key}") privateKey: String,
    @Value("\${fcm.client-email}") clientEmail: String,
    @Value("\${fcm.client-id}") clientId: String,
    @Value("\${fcm.type}") type: String,
    @Value("\${fcm.auth-url}") authUrl: String,
    @Value("\${fcm.token-url}") tokenUrl: String,
    @Value("\${fcm.auth-provider-x509-cert-url}") authProviderX509CertUrl: String,
    @Value("\${fcm.client-x509-cert-url}") clientX509CertUrl: String,
    @Value("\${fcm.universe-domain}") universeDomain: String
){
    private val fcmCredentials = FcmCredentials(
        type = type,
        project_id = projectId,
        private_key_id = privateKeyId,
        private_key = privateKey.replace("\\n", "\n"),  // 줄바꿈 문자 처리
        client_email = clientEmail,
        client_id = clientId,
        auth_uri = authUrl,
        token_uri = tokenUrl,
        auth_provider_x509_cert_url = authProviderX509CertUrl,
        client_x509_cert_url = clientX509CertUrl,
        universe_domain = universeDomain
    )

    fun toJson(): String{
        return try {
            ObjectMapper().writeValueAsString(fcmCredentials)
        }catch (e: JsonProcessingException){
            throw ToDeveloperDoException{ErrorCode.NOT_EXIST_BRANCH}
        }
    }
}