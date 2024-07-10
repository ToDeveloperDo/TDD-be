package io.junseok.todeveloperdo.oauth.git.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.toStringDateTime(): LocalDate = LocalDate.parse(
        this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    )