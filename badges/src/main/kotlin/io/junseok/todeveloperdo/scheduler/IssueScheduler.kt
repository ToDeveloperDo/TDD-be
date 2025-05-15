package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueReader
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueCreator
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class IssueScheduler(
    private val gitIssueReader: GitIssueReader,
    private val gitHubIssueCreator: GitHubIssueCreator,
    private val gitHubIssueProcessor: GitHubIssueProcessor,
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun makeIssue() {
        gitIssueReader.findGitIssueList().forEach { todoCreate ->
            val createIssueTemplate =
                gitHubIssueCreator.createIssueTemplate(todoCreate)
            gitHubIssueProcessor.createIssue(
                todoCreate.member.gitHubToken!!,
                todoCreate.member.gitHubUsername!!,
                todoCreate.member.gitHubRepo!!,
                createIssueTemplate
            )
        }
    }
}