package io.junseok.todeveloperdo.domains.todo.service

import io.junseok.todeveloperdo.domains.member.persistence.repository.MemberRepository
import io.junseok.todeveloperdo.domains.member.service.MemberService
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.domains.todo.persistence.repository.TodoListRepository
import io.junseok.todeveloperdo.exception.ErrorCode
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.oauth.git.GitHubService
import io.junseok.todeveloperdo.oauth.git.dto.request.GitHubIssuesRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.toTodoResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberTodoService(
    private val memberRepository: MemberRepository,
    private val todoListRepository: TodoListRepository,
    private val gitHubService: GitHubService,
    private val memberService: MemberService
) {
    @Transactional
    fun createTodoList(
        todoCreateRequest: TodoCreateRequest,
        username: String
    ): Long? {
        val member = (memberRepository.findByUsername(username)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_MEMBER })


        val gitHubIssuesRequest = GitHubIssuesRequest(
            title = "${todoCreateRequest.deadline} / ${todoCreateRequest.content}",
            body = """
                   TODO : ${todoCreateRequest.content}
                   MEMO : ${todoCreateRequest.memo}
                   TAG : ${todoCreateRequest.tag}
                   SHARE : ${todoCreateRequest.isShare}
                """.trimIndent(),
            assignees = listOf(member.username)
        )
        val createIssue = gitHubService.createIssue(
            member.gitHubToken,
            member.username,
            member.gitHubRepo!!,
            gitHubIssuesRequest
        )
        val memberTodoList = MemberTodoList(
            content = todoCreateRequest.content,
            memo = todoCreateRequest.memo,
            tag = todoCreateRequest.tag,
            deadline = todoCreateRequest.deadline,
            todoStatus = TodoStatus.PROCEED,
            isShare = todoCreateRequest.isShare,
            issueNumber = createIssue.number,
            member = member
        )
        return todoListRepository.save(memberTodoList).todoListId
    }

    @Transactional(readOnly = true)
    fun findTodoList(todoSearchRequest: TodoSearchRequest, username: String) =
        todoListRepository.findAllByDeadline(todoSearchRequest.deadline)
            .map { it.toTodoResponse() }

    @Transactional
    fun finishTodoList(todoListId: Long, username: String) {
        val memberTodoList = (todoListRepository.findByIdOrNull(todoListId)
            ?: throw ToDeveloperDoException { ErrorCode.NOT_EXIST_TODOLIST })
        val member = memberService.getMember(username)
        memberTodoList.updateTodoStatus()
        gitHubService.closeIssue(
            member.gitHubToken,
            username,
            member.gitHubRepo!!,
            memberTodoList.issueNumber!!
        )
    }

}