package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import io.junseok.todeveloperdo.domains.gitissue.persistence.repository.GitIssueRepository
import io.junseok.todeveloperdo.domains.gitissue.toTodoCreate
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class GitIssueReader(private val gitIssueRepository: GitIssueRepository) {
    @Transactional(readOnly = true)
    fun findGitIssueList() = gitIssueRepository.findAllByDeadline(LocalDate.now())
        .map { it.toTodoCreate() }

    @Transactional(readOnly = true)
    fun findGitIssueByTodoList(todoList: MemberTodoList) =
        gitIssueRepository.findByTodoList(todoList)

}