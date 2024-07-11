package io.junseok.todeveloperdo.domains.gitissue.service

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.gitissue.toTodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GitIssueService(private val gitIssueRepository: GitIssueRepository) {
    @Transactional
    fun saveGitIssue(todoCreateRequest: TodoCreateRequest,member: Member){
        val gitIssue = GitIssue(
            content = todoCreateRequest.content,
            memo = todoCreateRequest.memo!!,
            tag = todoCreateRequest.tag,
            deadline = todoCreateRequest.deadline,
            member = member
        )
        gitIssueRepository.save(gitIssue)
    }
}