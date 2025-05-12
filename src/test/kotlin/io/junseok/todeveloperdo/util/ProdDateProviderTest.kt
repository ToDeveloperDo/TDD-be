package io.junseok.todeveloperdo.util

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class ProdDateProviderTest : BehaviorSpec({

    val timeProvider = ProdDateProvider()

    Given("현재 LocalDate를 요청할 때") {
        When("now()를 호출하면") {
            val result = timeProvider.nowDate()

            Then("LocalDate.now()와 동일한 날짜가 반환되어야 한다") {
                result shouldBe LocalDate.now()
            }
        }
    }
    Given("현재 LocalDateTime을 요청할 때") {
        When("now()를 호출하면") {
            val result = timeProvider.nowDateTime()

            Then("LocalDateTime.now()와 동일한 날짜가 반환되어야 한다") {
                result.truncatedTo(ChronoUnit.SECONDS) shouldBe LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
            }
        }
    }
})
