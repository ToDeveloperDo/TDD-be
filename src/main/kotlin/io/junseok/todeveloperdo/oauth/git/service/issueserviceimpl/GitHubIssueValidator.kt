package io.junseok.todeveloperdo.oauth.git.service.issueserviceimpl

import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import org.springframework.stereotype.Component

@Component
class GitHubIssueValidator {
    fun isExist(memberTodoList: MemberTodoList){
        if (memberTodoList.issueNumber==null){
            throw ToDeveloperDoException{ErrorCode.NOT_EXIST_ISSUE}
        }
    }
}