package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberValidator
import io.junseok.todeveloperdo.oauth.git.service.readmeserviceimpl.ReadMeProcessor
import io.junseok.todeveloperdo.oauth.git.util.toGeneratorBearerToken
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReadMeScheduler(
    private val memberReader: MemberReader,
    private val readMeProcessor: ReadMeProcessor,
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun generatorReadMe() {
        memberReader.getAllMember()
            .forEach { member ->
                member.gitHubRepo?.let {
                    readMeProcessor.generatorReadMe(
                        member.gitHubToken!!.toGeneratorBearerToken(),
                        member,
                        member.gitHubRepo!!
                    )
                }
            }
    }
}