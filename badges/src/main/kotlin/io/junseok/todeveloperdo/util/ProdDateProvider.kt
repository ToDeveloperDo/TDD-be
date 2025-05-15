package io.junseok.todeveloperdo.util

import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ProdDateProvider : TimeProvider {
    override fun nowDate() = LocalDate.now()
    override fun nowDateTime(): LocalDateTime = LocalDateTime.now()

}