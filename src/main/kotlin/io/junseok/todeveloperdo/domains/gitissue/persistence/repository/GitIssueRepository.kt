package io.junseok.todeveloperdo.domains.gitissue.persistence.repository

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import org.springframework.data.jpa.repository.JpaRepository

interface GitIssueRepository : JpaRepository<GitIssue,Long> {
}