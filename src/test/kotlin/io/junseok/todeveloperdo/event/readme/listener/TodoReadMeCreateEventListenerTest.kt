package io.junseok.todeveloperdo.event.readme.listener

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.readme.dto.ReadMeEventRequest
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import java.time.LocalDate

class TodoReadMeCreateEventListenerTest : FunSpec({
    val readMeProcessor = mockk<ReadMeProcessor>()
    val today = LocalDate.of(2025, 5, 13)
    val timeProvider = StubDateProvider(today)
    val listener = TodoReadMeCreateEventListener(readMeProcessor, timeProvider)

    test("이벤트 요청을 수신하면 ReadMeProcessor가 호출되어 README가 생성된다") {
        val member = createMember(1, "appleId", "repo")
        val readMeEventRequest = createReadMeEventRequest(member)

        every { readMeProcessor.generatorReadMe(
            readMeEventRequest.member.gitHubToken!!.toGeneratorBearerToken(),
            readMeEventRequest.member,
            readMeEventRequest.member.gitHubRepo!!,
            timeProvider.nowDateTime()
        ) } just runs

        listener.create(readMeEventRequest)

        verify {
            readMeProcessor.generatorReadMe(
                readMeEventRequest.member.gitHubToken!!.toGeneratorBearerToken(),
                readMeEventRequest.member,
                readMeEventRequest.member.gitHubRepo!!,
                timeProvider.nowDateTime()
            )
        }
    }
})

fun createReadMeEventRequest(member: Member) = ReadMeEventRequest(member = member)