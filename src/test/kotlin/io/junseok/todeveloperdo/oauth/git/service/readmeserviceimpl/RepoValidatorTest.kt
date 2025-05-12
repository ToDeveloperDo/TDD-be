package io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl

import io.junseok.todeveloperdo.exception.ErrorCode.INVALID_REPO_NAME_GAP
import io.junseok.todeveloperdo.exception.ErrorCode.INVALID_REPO_NAME_KOREAN
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepoValidatorTest : FunSpec({
    val repoValidator = RepoValidator()
    test("공백이 포함된 저장소 이름이면 예외가 발생해야 한다") {
        val invalidName = "my repo"
        throwsWith<ToDeveloperDoException>(
            {
                repoValidator.existGap(invalidName)
            },
            {
                ex -> ex.errorCode shouldBe INVALID_REPO_NAME_GAP
            }
        )
    }

    test("정상적인 저장소 이름이면 예외가 발생하지 않아야 한다") {
        val validName = "my-repo"
        shouldNotThrow<ToDeveloperDoException> {
            repoValidator.existGap(validName)
        }
    }

    test("한글이 포함된 저장소 이름이면 예외가 발생해야 한다") {
        val invalidName = "레포이름"
        throwsWith<ToDeveloperDoException>(
            {
                repoValidator.existKorean(invalidName)
            },
            {
                    ex -> ex.errorCode shouldBe INVALID_REPO_NAME_KOREAN
            }
        )
    }

    test("영문 저장소 이름이면 예외가 발생하지 않아야 한다") {
        val validName = "my-repo"
        shouldNotThrow<ToDeveloperDoException> {
            repoValidator.existKorean(validName)
        }
    }
    test("유효한 저장소 이름은 예외를 발생시키지 않아야 한다") {
        val validName = "my-repo"
        shouldNotThrow<ToDeveloperDoException> {
            repoValidator.isValid(validName)
        }
    }


})
