package io.junseok.todeveloperdo.oauth.apple.config

import feign.form.FormEncoder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class AppleWithdrawConfigTest : FunSpec({
    val config = AppleWithdrawConfig()

    test("feignFormEncoder는 FormEncoder를 반환해야 한다") {
        config.feignFormEncoder().shouldBeInstanceOf<FormEncoder>()
    }

})
