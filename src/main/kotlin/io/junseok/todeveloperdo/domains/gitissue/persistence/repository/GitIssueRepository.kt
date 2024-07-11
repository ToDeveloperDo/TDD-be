package io.junseok.todeveloperdo.domains.gitissue.persistence.repository

import io.junseok.todeveloperdo.domains.gitissue.persistence.entity.GitIssue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GitIssueRepository : JpaRepository<GitIssue,Long> {

    @Query("select g from GitIssue g where g.isCreate=false")
    fun findAllUnSendIssue():List<GitIssue>
}