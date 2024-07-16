package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.Objects
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface TodoListRepository : JpaRepository<MemberTodoList, Long> {
    fun findByDeadlineAndTodoStatusAndMember(
        date: LocalDate,
        todoStatus: TodoStatus,
        member: Member
    ): List<MemberTodoList>

    fun existsByTodoListIdAndMember(todoListId: Long, member: Member): Boolean

    @Query(
        "SELECT " +
                "new io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse(t.deadline, COUNT(t)) " +
                "FROM MemberTodoList t " +
                "WHERE FUNCTION('MONTH', t.deadline) = :month AND" +
                " FUNCTION('YEAR', t.deadline) = :year AND" +
                " t.member = :member GROUP BY t.deadline"
    )

    fun findAllByTodoListMonthAndYear(
        @Param(value = "month") month: Int,
        @Param(value = "year") year: Int,
        @Param(value = "member") member: Member
    ): List<TodoCountResponse>
}