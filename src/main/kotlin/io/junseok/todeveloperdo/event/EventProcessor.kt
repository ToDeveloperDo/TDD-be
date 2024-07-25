package io.junseok.todeveloperdo.event

import io.junseok.todeveloperdo.domains.gitissue.TodoCreate
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.event.issue.IssueEventProcessor
import io.junseok.todeveloperdo.event.issue.dto.request.IssueEventRequest
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component

@Component
class EventProcessor(
    private val issueEventProcessor: IssueEventProcessor,
    private val readMeEventProcessor: ReadMeEventProcessor
) {
    fun createIssue(member: Member, todoRequest: TodoRequest): IssueEventRequest {
        return issueEventProcessor.create(member, todoRequest)
    }

    fun closeIssueWithReadMe(member: Member, issueNumber: Int, state: String) {
        issueEventProcessor.close(member, issueNumber, state)
        readMeEventProcessor.create(member)
    }

    fun updateIssueWithReadMe(member: Member, issueNumber: Int, todoCreate: TodoCreate) {
        issueEventProcessor.update(member,issueNumber,todoCreate)
        readMeEventProcessor.create(member)
    }
}
