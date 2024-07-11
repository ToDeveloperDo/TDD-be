package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.gitissue.toTodoCreate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GitIssueReader(private val gitIssueRepository: GitIssueRepository) {
    @Transactional(readOnly = true)
    fun findGitIssueList() = gitIssueRepository.findAllUnSendIssue()
        .map { it.toTodoCreate() }

}