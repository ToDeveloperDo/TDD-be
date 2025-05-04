package io.junseok.todeveloperdo.global.rabbitmq

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMQProducer(
    private val rabbitTemplate: RabbitTemplate,
) {

    fun sendMessage(queueName: String, message: String) {
        println("Sending message: queue: $queueName")
        rabbitTemplate.convertAndSend(queueName,message)
    }
}
