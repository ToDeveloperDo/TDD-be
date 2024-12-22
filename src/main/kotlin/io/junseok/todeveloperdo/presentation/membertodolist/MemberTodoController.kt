package io.junseok.todeveloperdo.presentation.membertodolist

import io.junseok.todeveloperdo.domains.todo.service.MemberTodoService
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_CLOSED
import io.junseok.todeveloperdo.oauth.git.service.GitHubService.Companion.ISSUE_OPEN
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCountRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoDateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequests
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import io.junseok.todeveloperdo.scheduler.FcmScheduler
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/todo")
@CrossOrigin
class MemberTodoController(
    private val memberTodoService: MemberTodoService,
    private val fcmScheduler: FcmScheduler
) {

    /**
     * NOTE
     * 할 일 등록 API
     */
    @PostMapping
    fun registerTodoList(
        @RequestBody todoRequests: TodoRequests,
        principal: Principal
    ): ResponseEntity<Long> =
        ResponseEntity.ok(memberTodoService.createTodoList(todoRequests.todos, principal.name))

    /**
     * NOTE
     * 해당 요일에 있는 할 일 목록 조회
     */
    @PostMapping("/list")
    fun showTodoList(
        @RequestBody todoDateRequest: TodoDateRequest,
        principal: Principal
    ): ResponseEntity<List<TodoResponse>> =
        ResponseEntity.ok(memberTodoService.findTodoLists(todoDateRequest, principal.name))

    /**
     * NOTE
     * 한 일 체크
     */
    @PatchMapping("/done/{todoListId}")
    fun checkTodoList(@PathVariable todoListId: Long, principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberTodoService.finishTodoList(todoListId, principal.name, ISSUE_CLOSED))

    /**
     * NOTE
     * TODOLIST 수정
     */
    @PatchMapping("/change/{todoListId}")
    fun updateTodoList(
        @PathVariable todoListId: Long,
        @RequestBody todoRequest: TodoRequest,
        principal: Principal
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(memberTodoService.modifyTodoList(todoListId, todoRequest, principal.name))

    @DeleteMapping("/{todoListId}")
    fun deleteTodoList(@PathVariable todoListId: Long, principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberTodoService.removeTodoList(todoListId, principal.name, ISSUE_CLOSED))

    @PostMapping("/count")
    fun countTodoList(
        @RequestBody todoCountRequest: TodoCountRequest,
        principal: Principal
    ): ResponseEntity<List<TodoCountResponse>> =
        ResponseEntity.ok(memberTodoService.calculateTodoList(todoCountRequest, principal.name))

    @PatchMapping("/proceed/{todoListId}")
    fun proceedTodoList(
        @PathVariable todoListId: Long,
        principal: Principal
    ): ResponseEntity<Unit> =
        ResponseEntity.ok(
            memberTodoService.unFinishedTodoList(todoListId, principal.name, ISSUE_OPEN)
        )

    @GetMapping("/test")
    fun test() {
        fcmScheduler.sendEveningNotificationScheduler()
    }
}