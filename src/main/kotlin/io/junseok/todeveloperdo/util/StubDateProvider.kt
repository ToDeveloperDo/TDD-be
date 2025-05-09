package io.junseok.todeveloperdo.util

import java.time.LocalDate

class StubDateProvider(val deadline: LocalDate) : TimeProvider {
    override fun now(): LocalDate = deadline
}