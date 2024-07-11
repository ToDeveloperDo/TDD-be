package io.junseok.todeveloperdo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
class ToDeveloperDoApplication
fun main(args: Array<String>) {
    runApplication<ToDeveloperDoApplication>(*args)
}
