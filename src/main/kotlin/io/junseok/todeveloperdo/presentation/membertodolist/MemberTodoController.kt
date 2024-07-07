package io.junseok.todeveloperdo.presentation.membertodolist

import io.junseok.todeveloperdo.domains.todo.service.MemberTodoService
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoCreateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/api/todo")
@CrossOrigin
class MemberTodoController(
    private val memberTodoService: MemberTodoService
) {
    @PostMapping
    fun registerTodoList(
        @RequestBody todoCreateRequest: TodoCreateRequest,
        principal: Principal
    ): ResponseEntity<Long> =
        ResponseEntity.ok(memberTodoService.createTodoList(todoCreateRequest, principal.name))

}