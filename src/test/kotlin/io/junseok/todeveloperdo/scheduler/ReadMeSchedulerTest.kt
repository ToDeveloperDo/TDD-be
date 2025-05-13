package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import io.junseok.todeveloperdo.util.StubDateProvider
import io.kotest.core.spec.style.FunSpec
import io.mockk.*
import java.time.LocalDate

class ReadMeSchedulerTest : FunSpec({
    val memberReader = mockk<MemberReader>()
    val readMeProcessor = mockk<ReadMeProcessor>()
    val today = LocalDate.of(2025,5,13)
    val timeProvider = StubDateProvider(today)
    val readMeScheduler = ReadMeScheduler(memberReader, readMeProcessor, timeProvider)

    test("레포가 존재한다면 ReadMe가 정상적으로 생성이 되어야한다.") {
        val inValidMember = createMember(1, "appleId")
        val validMember = createMember(1, "appleId", "repo")
        val memberList = listOf(inValidMember, validMember)

        every { memberReader.getAllMember() } returns memberList
        every {
            readMeProcessor.generatorReadMe(
                validMember.gitHubToken!!.toGeneratorBearerToken(),
                validMember,
                validMember.gitHubRepo!!,
                timeProvider.nowDateTime()
            )
        } just runs

        readMeScheduler.generatorReadMe()

        memberList.filter { it.gitHubRepo != null }.forEach {
            verify(exactly = 1) {
                readMeProcessor.generatorReadMe(
                    it.gitHubToken!!.toGeneratorBearerToken(),
                    it,
                    it.gitHubRepo!!,
                    timeProvider.nowDateTime()
                )
            }
        }
    }
})
