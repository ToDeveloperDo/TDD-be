package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GitIssueSaver(private val gitIssueRepository: GitIssueRepository) {
    @Transactional
    fun save(gitIssue: GitIssue) = gitIssueRepository.save(gitIssue)
}