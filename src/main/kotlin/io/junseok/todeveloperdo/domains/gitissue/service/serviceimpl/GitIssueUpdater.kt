package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class GitIssueUpdater(
    private val issueReader: GitIssueReader,
    private val eventProcessor: EventProcessor,
    private val readMeEventProcessor: ReadMeEventProcessor
) {
    //할 일
    @Transactional
    fun update(member: Member,todoList: MemberTodoList,todoRequest: TodoRequest){
        val gitIssue = issueReader.findGitIssueByTodoList(todoList)
        gitIssue.updateDeadline(todoRequest.deadline)
        if(LocalDate.now()==gitIssue.deadline && todoList.issueNumber==null){
            eventProcessor.createIssue(member, todoRequest)
            readMeEventProcessor.create(member)
        }
    }
}