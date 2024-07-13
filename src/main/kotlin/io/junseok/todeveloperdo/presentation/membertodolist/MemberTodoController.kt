package io.junseok.todeveloperdo.presentation.membertodolist

import io.junseok.todeveloperdo.domains.todo.service.MemberTodoService
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoSearchRequest
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/todo")
@CrossOrigin
class MemberTodoController(
    private val memberTodoService: MemberTodoService
) {

    /**
     * NOTE
     * 할 일 등록 API
     */
    @PostMapping
    fun registerTodoList(
        @RequestBody todoCreateRequest: TodoCreateRequest,
        principal: Principal
    ): ResponseEntity<Long> =
        ResponseEntity.ok(memberTodoService.createTodoList(todoCreateRequest, principal.name))

    /**
     * NOTE
     * 해당 요일에 있는 할 일 목록 조회
     */
    @PostMapping("/list")
    fun showTodoList(
        @RequestBody todoSearchRequest: TodoSearchRequest,
        principal: Principal
    ): ResponseEntity<List<TodoResponse>> =
        ResponseEntity.ok(memberTodoService.findTodoLists(todoSearchRequest, principal.name))

    /**
     * NOTE
     * 한 일 체크
     */
    @PatchMapping("/{todoListId}")
    fun checkTodoList(@PathVariable todoListId: Long, principal: Principal): ResponseEntity<Unit> =
        ResponseEntity.ok(memberTodoService.finishTodoList(todoListId, principal.name))
}