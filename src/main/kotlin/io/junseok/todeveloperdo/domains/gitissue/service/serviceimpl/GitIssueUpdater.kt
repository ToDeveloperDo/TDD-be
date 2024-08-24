package io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoUpdater
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Component
class GitIssueUpdater(
    private val issueReader: GitIssueReader,
    private val eventProcessor: EventProcessor,
    private val readMeEventProcessor: ReadMeEventProcessor,
    private val todoUpdater: TodoUpdater
) {
    //할 일
    @Transactional
    fun update(member: Member, todoList: MemberTodoList, todoRequest: TodoRequest) {
        val gitIssue = issueReader.findGitIssueByTodoList(todoList)
        gitIssue.update(todoRequest)
       /* if (LocalDate.now() == gitIssue.deadline && todoList.issueNumber == null) {
            val issueNumber = (
                    eventProcessor.createIssue(member, todoRequest).issueNumber.get()
                ?: throw ToDeveloperDoException { ErrorCode.FAILED_TO_GENERATE_ISSUE }
                    )
            todoUpdater.modifyIssueNumber(issueNumber, todoList)
        }else if (LocalDate.now() != gitIssue.deadline && todoList.issueNumber != null) {
            //이슈가 closed 되어야함
        }

        readMeEventProcessor.create(member)*/
    }
}