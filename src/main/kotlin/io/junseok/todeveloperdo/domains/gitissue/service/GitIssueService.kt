package io.junseok.todeveloperdo.domains.gitissue.service

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GitIssueService(private val gitIssueRepository: GitIssueRepository) {
    @Transactional
    fun saveGitIssue(todoRequest: TodoRequest, member: Member){
        val gitIssue = GitIssue(
            content = todoRequest.content,
            memo = todoRequest.memo!!,
            tag = todoRequest.tag,
            deadline = todoRequest.deadline,
            member = member
        )
        gitIssueRepository.save(gitIssue)
    }
}