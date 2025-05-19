package io.junseok.todeveloperdo.aop.annotation.update

import io.junseok.todeveloperdo.domains.gitissue.service.serviceimpl.GitIssueReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoReader
import io.junseok.todeveloperdo.domains.todo.service.serviceimpl.TodoUpdater
import io.junseok.todeveloperdo.event.EventProcessor
import io.junseok.todeveloperdo.event.issue.IssueEventProcessor
import io.junseok.todeveloperdo.event.readme.ReadMeEventProcessor
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_CLOSED
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_OPEN
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.toTodoCreate
import io.junseok.todeveloperdo.util.TimeProvider
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class UpdateAspect(
    private val eventProcessor: EventProcessor,
    private val memberReader: MemberReader,
    private val todoReader: TodoReader,
    private val gitIssueReader: GitIssueReader,
    private val todoUpdater: TodoUpdater,
    private val issueEventProcessor: IssueEventProcessor,
    private val readMeEventProcessor: ReadMeEventProcessor,
    private val timeProvider: TimeProvider
) {
    @After("@annotation(UpdateEvent)")
    fun update(joinPoint: JoinPoint) {
        val args = joinPoint.args
        val todoListId = args[0] as Long
        val todoRequest = args[1] as TodoRequest
        val username = args[2] as String

        val member = memberReader.getMember(username)
        val findTodoList = todoReader.findTodoList(todoListId)
        val gitIssue = gitIssueReader.findGitIssueByTodoList(findTodoList)
        val issueNumber = findTodoList.issueNumber

        if (issueNumber == null) {
            val issueEventRequest = eventProcessor.createIssue(member, todoRequest)
            val createdIssueNumber = issueEventRequest.issueNumber.get()
                ?: throw ToDeveloperDoException{ErrorCode.FAILED_TO_GENERATE_ISSUE}

            todoUpdater.modifyIssueNumber(createdIssueNumber, findTodoList)
        }

        if (issueNumber != null && timeProvider.nowDate() == gitIssue.deadline) {
            issueEventProcessor.close(member, issueNumber, ISSUE_OPEN)
        }

        if (issueNumber != null && timeProvider.nowDate() != gitIssue.deadline) {
            issueEventProcessor.close(member, issueNumber, ISSUE_CLOSED)
        }

        // 이슈가 존재하면 업데이트
        if (issueNumber != null) {
            eventProcessor.updateIssueWithReadMe(
                member,
                issueNumber,
                todoRequest.toTodoCreate(member)
            )
        }

        readMeEventProcessor.create(member)
    }
}