package io.junseok.todeveloperdo.aop.annotation.create

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.createTodoRequest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.aspectj.lang.ProceedingJoinPoint

class CreateReadMeAspectTest : FunSpec({
    val readMeEventProcessor = mockk<ReadMeEventProcessor>()
    val memberReader = mockk<MemberReader>()
    val createReadMeAspect = CreateReadMeAspect(readMeEventProcessor, memberReader)

    test("username을 기준으로 member를 조회하고 ReadMe를 생성한 뒤, 원래 결과를 반환한다") {
        val todoRequests = listOf(createTodoRequest())
        val username = "username"
        val proceedResult = "success"
        val member = createMember(1, "appleId", "repo")
        val joinPoint = mockk<ProceedingJoinPoint> {
            every { args } returns arrayOf(todoRequests, username)
            every { proceed() } returns proceedResult
        }

        every { memberReader.getMember(username) } returns member
        every { readMeEventProcessor.create(member) } just runs

        val result = createReadMeAspect.aroundCreateReadMe(joinPoint)

        result shouldBe proceedResult
    }
})
