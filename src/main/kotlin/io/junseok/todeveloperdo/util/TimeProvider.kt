package io.junseok.todeveloperdo.util

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}