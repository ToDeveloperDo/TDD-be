package io.junseok.todeveloperdo.event.readme

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.readme.listener.createReadMeEventRequest
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import org.springframework.context.ApplicationEventPublisher

class ReadMeEventProcessorTest : FunSpec({
    val eventPublisher = mockk<ApplicationEventPublisher>()
    val readMeEventProcessor = ReadMeEventProcessor(eventPublisher)

    test("create() 호출 시 ReadMeEventRequest 이벤트가 발행된다") {
        val member = createMember(1, "appleId", "repo")
        val readMeEventRequest = createReadMeEventRequest(member)
        every { eventPublisher.publishEvent(readMeEventRequest) } just runs

        readMeEventProcessor.create(member)

        verify { eventPublisher.publishEvent(readMeEventRequest) }
    }
})
