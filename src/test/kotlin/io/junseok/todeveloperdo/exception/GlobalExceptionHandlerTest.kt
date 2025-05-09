package io.junseok.todeveloperdo.exception

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class GlobalExceptionHandlerTest : FunSpec({

    test("ToDeveloperDoException을 처리하면 ErrorResponseEntity가 반환된다") {
        val handler = GlobalExceptionHandler()
        val exception = ToDeveloperDoException { ErrorCode.INVALID_TODOLIST }
        val response = handler.errorCodeResponseEntity(exception)

        response.statusCode.value() shouldBe exception.errorCode.status
        response.body.code shouldBe "INVALID_TODOLIST"
        response.body.message shouldBe "본인의 TODOLIST만 수정가능합니다!"
    }
})
