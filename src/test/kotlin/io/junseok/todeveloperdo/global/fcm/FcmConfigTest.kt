package io.junseok.todeveloperdo.global.fcm

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import java.io.InputStream

class FcmConfigTest : StringSpec({

    beforeTest {
        MockKAnnotations.init(this)
        mockkStatic(GoogleCredentials::class)
        mockkStatic(FirebaseApp::class)
        mockkStatic(FirebaseMessaging::class)
    }

    afterTest {
        unmockkAll()
    }

    "Firebase Messaging Bean이 유효한 인증 정보로 생성되어야 한다" {
        val fcmJsonConfig = mockk<FcmJsonConfig>()
        val mockCredentials = mockk<GoogleCredentials>()
        val mockApp = mockk<FirebaseApp>()
        val mockMessaging = mockk<FirebaseMessaging>()

        val dummyJson = """{"type":"service_account","project_id":"test-project"}"""

        every { fcmJsonConfig.toJson() } returns dummyJson
        every { GoogleCredentials.fromStream(any()) } returns mockCredentials
        every { FirebaseApp.getApps() } returns emptyList()

        every {
            FirebaseApp.initializeApp(match<FirebaseOptions> { true })
        } returns mockApp

        every { FirebaseMessaging.getInstance() } returns mockMessaging

        val optionsBuilder = mockk<FirebaseOptions.Builder>()
        val options = mockk<FirebaseOptions>()

        mockkStatic(FirebaseOptions::class)
        every { FirebaseOptions.builder() } returns optionsBuilder
        every { optionsBuilder.setCredentials(any()) } returns optionsBuilder
        every { optionsBuilder.build() } returns options

        val fcmConfig = FcmConfig(fcmJsonConfig)
        val result = fcmConfig.firebaseMessaging()

        result shouldNotBe null

        verify { fcmJsonConfig.toJson() }
        verify { GoogleCredentials.fromStream(any()) }
        verify { FirebaseOptions.builder() }
        verify { optionsBuilder.setCredentials(any()) }
        verify { optionsBuilder.build() }
        verify { FirebaseApp.getApps() }
        verify { FirebaseApp.initializeApp(match<FirebaseOptions> { true }) }
        verify { FirebaseMessaging.getInstance() }
    }

    "이미 Firebase 앱이 초기화된 경우 기존 인스턴스를 사용해야 한다" {
        val fcmJsonConfig = mockk<FcmJsonConfig>()
        val mockCredentials = mockk<GoogleCredentials>()
        val mockApp = mockk<FirebaseApp>()
        val mockMessaging = mockk<FirebaseMessaging>()

        val dummyJson = """{"type":"service_account","project_id":"test-project"}"""

        every { fcmJsonConfig.toJson() } returns dummyJson
        every { GoogleCredentials.fromStream(any()) } returns mockCredentials
        every { FirebaseApp.getApps() } returns listOf(mockApp)
        every { FirebaseApp.getInstance() } returns mockApp
        every { FirebaseMessaging.getInstance() } returns mockMessaging

        val optionsBuilder = mockk<FirebaseOptions.Builder>()
        val options = mockk<FirebaseOptions>()

        mockkStatic(FirebaseOptions::class)
        every { FirebaseOptions.builder() } returns optionsBuilder
        every { optionsBuilder.setCredentials(any()) } returns optionsBuilder
        every { optionsBuilder.build() } returns options

        val fcmConfig = FcmConfig(fcmJsonConfig)
        val result = fcmConfig.firebaseMessaging()

        result shouldNotBe null

        verify { fcmJsonConfig.toJson() }
        verify { GoogleCredentials.fromStream(any()) }
        verify { FirebaseOptions.builder() }
        verify { optionsBuilder.setCredentials(any()) }
        verify { optionsBuilder.build() }
        verify { FirebaseApp.getApps() }
        verify { FirebaseApp.getInstance() }
        verify { FirebaseMessaging.getInstance() }
    }

    "초기화 실패 시 예외가 발생해야 한다" {
        val fcmJsonConfig = mockk<FcmJsonConfig>()

        val dummyJson = """{"type":"service_account","project_id":"test-project"}"""

        every { fcmJsonConfig.toJson() } returns dummyJson
        every { GoogleCredentials.fromStream(any<InputStream>()) } throws RuntimeException("인증 정보 오류")

        val fcmConfig = FcmConfig(fcmJsonConfig)

        throwsWith<IllegalStateException>(
            {
                fcmConfig.firebaseMessaging()
            },
            { ex ->
                ex.message shouldBe "Failed to initialize Firebase Messaging"
            }
        )
    }
})