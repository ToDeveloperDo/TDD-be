package io.junseok.todeveloperdo.scheduler

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueReader
import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueUpdater
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueCreator
import io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl.GitHubIssueProcessor
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class IssueScheduler(
    private val gitIssueReader: GitIssueReader,
    private val gitHubIssueCreator: GitHubIssueCreator,
    private val gitHubIssueProcessor: GitHubIssueProcessor,
    private val gitIssueUpdater: GitIssueUpdater
) {
    @Scheduled(cron = "0 0 0 * * *")
    fun makeIssue() {
        val findGitIssueList = gitIssueReader.findGitIssueList()
        findGitIssueList.forEach { todoCreate ->
                val createIssueTemplate =
                    gitHubIssueCreator.createIssueTemplate(todoCreate)
                gitHubIssueProcessor.createIssue(
                    todoCreate.member.gitHubToken,
                    todoCreate.member.username,
                    todoCreate.member.gitHubRepo!!,
                    createIssueTemplate
                )
            }
        gitIssueUpdater.successCreateIssue(findGitIssueList)
    }


}