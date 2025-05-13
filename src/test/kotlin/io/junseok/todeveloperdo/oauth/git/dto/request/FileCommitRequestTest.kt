package io.junseok.todeveloperdo.oauth.git.dto.request

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.time.LocalDate
import java.util.*

class FileCommitRequestTest : FunSpec({
    test("fileCommitRequestInit은 owner, content, branch, sha를 기반으로 생성된다") {
        val owner = "junseok"
        val content = "my content"
        val branch = "main"
        val sha = null

        val result = FileCommitRequest.fileCommitRequestInit(owner, content, branch, sha)

        result.message shouldStartWith "feat : junseok's ${LocalDate.now()} TodoList"
        result.content shouldBe Base64.getEncoder().encodeToString(content.toByteArray())
        result.branch shouldBe branch
        result.sha shouldBe sha
    }

})
