package io.junseok.todeveloperdo.util

import java.time.LocalDate
import java.time.LocalDateTime

interface TimeProvider {
    fun nowDate(): LocalDate
    fun nowDateTime(): LocalDateTime

}