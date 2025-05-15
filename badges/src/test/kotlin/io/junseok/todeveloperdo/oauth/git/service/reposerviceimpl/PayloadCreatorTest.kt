package io.junseok.todeveloperdo.oauth.git.service.reposerviceimpl

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PayloadCreatorTest : FunSpec({
    val payloadCreator = PayloadCreator()

    test("payload에서 저장소 이름과 사용자 이름을 정상적으로 파싱해야 한다") {
        val payload = mapOf(
            "repository" to mapOf(
                "name" to "test-repo",
                "owner" to mapOf("login" to "junseok")
            )
        )

        val result = payloadCreator.create(payload)

        result.newRepoName shouldBe "test-repo"
        result.username shouldBe "junseok"
    }

    test("payload에 필드가 없으면 예외가 발생해야 한다") {
        val brokenPayload = mapOf<String, Any>()

        shouldThrow<NullPointerException> {
            payloadCreator.create(brokenPayload)
        }
    }

})
