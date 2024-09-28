package io.junseok.todeveloperdo.oauth.git.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

fun LocalDateTime.toStringDateTime(): LocalDate = LocalDate.parse(
    this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
)

fun LocalDateTime.toKoreanDayName(): String =
    this.dayOfWeek.getDisplayName(
        TextStyle.SHORT,
        Locale.KOREAN
    )