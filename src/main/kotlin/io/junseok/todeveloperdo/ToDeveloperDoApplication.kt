package io.junseok.todeveloperdo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@EnableScheduling
@EnableAspectJAutoProxy
@EnableAsync
class ToDeveloperDoApplication
fun main(args: Array<String>) {
    runApplication<ToDeveloperDoApplication>(*args)
}
