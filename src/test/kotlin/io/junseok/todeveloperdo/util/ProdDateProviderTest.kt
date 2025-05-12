package io.junseok.todeveloperdo.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ProdDateProviderTest : BehaviorSpec({

    val timeProvider = ProdDateProvider()

    Given("현재 시간을 요청할 때") {
        When("now()를 호출하면") {
            val result = timeProvider.now()

            Then("LocalDate.now()와 동일한 날짜가 반환되어야 한다") {
                result shouldBe LocalDate.now()
            }
        }
    }
})
