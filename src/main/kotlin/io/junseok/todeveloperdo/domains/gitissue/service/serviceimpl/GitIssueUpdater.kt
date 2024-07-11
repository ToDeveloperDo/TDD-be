package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.gitissue.toTodoCreate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GitIssueUpdater(
    private val gitIssueReader: GitIssueReader
) {
    @Transactional
    fun successCreateIssue(gitIssueList: List<TodoCreate>) {
        gitIssueList.forEach { gitIssue ->
            gitIssueReader.findGitIssue(gitIssue.issueId!!).toTodoCreate()
        }
    }
}