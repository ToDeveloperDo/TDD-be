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
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import java.time.LocalDate

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

        // 수정했는데 다른 날에서 오늘인 경우 -> create, issueNumber가 null아 아닌 경우 open
        if (LocalDate.now() == gitIssue.deadline && findTodoList.issueNumber == null){
            val issueNumber = (
                    eventProcessor.createIssue(member, todoRequest).issueNumber.get()
                        ?: throw ToDeveloperDoException { ErrorCode.FAILED_TO_GENERATE_ISSUE }
                    )
            todoUpdater.modifyIssueNumber(issueNumber, findTodoList)
        }else if(LocalDate.now() == gitIssue.deadline && findTodoList.issueNumber != null){
            issueEventProcessor.close(member, findTodoList.issueNumber!!, ISSUE_OPEN)
        }

        // 요일은 수정안한경우(오늘이거나 다른 날 인경우)
        findTodoList.issueNumber?.let {
            eventProcessor.updateIssueWithReadMe(
                member,
                findTodoList.issueNumber!!,
                todoRequest.toTodoCreate(member)
            )
        }

        // 오늘에서 다른 날로 수정한 경우 -> closed
        if(LocalDate.now()!=gitIssue.deadline && findTodoList.issueNumber != null){
            issueEventProcessor.close(member, findTodoList.issueNumber!!, ISSUE_CLOSED)
        }

        readMeEventProcessor.create(member)
    }
}