package io.junseok.todeveloperdo.event.readme

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.event.readme.dto.ReadMeEventRequest
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ReadMeEventProcessor(
    private val eventPublisher: ApplicationEventPublisher
) {
    fun create(member: Member){
        eventPublisher.publishEvent(ReadMeEventRequest(member))
    }
}