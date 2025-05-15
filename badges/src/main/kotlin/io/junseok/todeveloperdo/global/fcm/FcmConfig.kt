package io.junseok.todeveloperdo.global.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream

@Configuration
class FcmConfig(
    private val fcmJsonConfig: FcmJsonConfig
) {
    private val logger = LoggerFactory.getLogger(FcmConfig::class.java)

    @Bean
    fun firebaseMessaging(): FirebaseMessaging {
        try {
            val firebaseOptions = FirebaseOptions.builder()
                .setCredentials(
                    GoogleCredentials.fromStream(
                        ByteArrayInputStream(fcmJsonConfig.toJson().toByteArray())
                    )
                )
                .build()

            // FirebaseApp 초기화 확인 및 필요시 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(firebaseOptions)
            } else {
                FirebaseApp.getInstance() // 이미 초기화된 경우 인스턴스 가져오기
            }

            return FirebaseMessaging.getInstance() // FirebaseMessaging 객체 반환

        } catch (e: Exception) {
            logger.error("Firebase messaging initialization error: ${e.message}")
            throw IllegalStateException("Failed to initialize Firebase Messaging", e)
        }
    }

}