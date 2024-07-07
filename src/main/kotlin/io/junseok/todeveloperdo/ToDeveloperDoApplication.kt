package io.junseok.todeveloperdo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication//(exclude = [SecurityAutoConfiguration::class])
@EnableFeignClients
@EnableJpaAuditing
class ToDeveloperDoApplication
fun main(args: Array<String>) {
    runApplication<ToDeveloperDoApplication>(*args)
}
