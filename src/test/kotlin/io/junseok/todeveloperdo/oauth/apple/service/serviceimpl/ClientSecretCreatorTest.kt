package io.junseok.todeveloperdo.oauth.apple.service.serviceimpl

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@Ignored
class ClientSecretCreatorTest : FunSpec({
    test("정상적인 JWT를 생성한다") {
        val privateKey = System.getenv("APPLE_PRIVATE_KEY")
            ?.replace("\\n", "\n")
            ?: throw IllegalStateException("APPLE_PRIVATE_KEY not set")

        val creator = ClientSecretCreator(
            clientId = "com.example.app",
            teamId = "TEAMID123",
            keyId = "KEYID456",
            privateKey = privateKey
        )

        val jwt = creator.createClientSecret()
        jwt shouldNotBe null
        jwt.split(".").size shouldBe 3
    }
})
