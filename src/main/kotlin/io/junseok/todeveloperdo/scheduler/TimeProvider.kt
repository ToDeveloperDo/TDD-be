package io.junseok.todeveloperdo.scheduler

import java.time.LocalDate

interface TimeProvider {
    fun now(): LocalDate
}