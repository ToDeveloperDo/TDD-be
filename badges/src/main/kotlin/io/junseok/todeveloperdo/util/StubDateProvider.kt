package io.junseok.todeveloperdo.util

import java.time.LocalDate
import java.time.LocalDateTime

class StubDateProvider(
    val deadline: LocalDate,
) : TimeProvider {
    override fun nowDate(): LocalDate = deadline
    override fun nowDateTime(): LocalDateTime = deadline.atStartOfDay()

}