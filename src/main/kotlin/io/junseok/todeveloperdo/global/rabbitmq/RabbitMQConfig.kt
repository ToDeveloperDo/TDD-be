package io.junseok.todeveloperdo.global.rabbitmq

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun myQueue(): Queue {
        return QueueBuilder
            .durable("todo-queue")
            .deadLetterExchange("dlx")
            .deadLetterRoutingKey("retry-queue")
            .build()
    }

    @Bean
    fun retryQueue(): Queue {
        return QueueBuilder
            .durable("retry-queue")
            .withArgument("x-message-ttl", 5000)
            .withArgument("x-dead-letter-exchange", "")
            .withArgument("x-dead-letter-routing-key", "todo-queue")
            .build()
    }

    @Bean
    fun deadMessageExchange() = DirectExchange("dlx")

    @Bean
    fun deadMessageBinding(): Binding {
        return BindingBuilder
            .bind(retryQueue())
            .to(deadMessageExchange())
            .with("retry-queue")
    }
}
