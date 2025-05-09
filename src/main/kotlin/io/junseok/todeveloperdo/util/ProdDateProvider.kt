package io.junseok.todeveloperdo.util

import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProdDateProvider : TimeProvider {
    override fun now() = LocalDate.now()
}