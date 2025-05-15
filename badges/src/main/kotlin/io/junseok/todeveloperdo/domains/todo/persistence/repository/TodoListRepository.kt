package io.junseok.todeveloperdo.domains.todo.persistence.repository

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.domains.todo.persistence.entity.TodoStatus
import io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface TodoListRepository : JpaRepository<MemberTodoList, Long> {
    fun findByDeadlineAndMember(
        date: LocalDate,
        member: Member,
    ): List<MemberTodoList>

    fun findByDeadlineAndTodoStatusAndMember(
        date: LocalDate,
        todoStatus: TodoStatus,
        member: Member,
    ): List<MemberTodoList>

    fun existsByTodoListIdAndMember(todoListId: Long, member: Member): Boolean

    fun findByMemberAndDeadlineBetween(
        member: Member,
        startDt: LocalDate,
        endDt: LocalDate,
    ): List<MemberTodoList>

    fun findAllByDeadlineAndTodoStatus(
        deadline: LocalDate,
        todoStatus: TodoStatus,
    ): List<MemberTodoList>

    @Query(
        """
        SELECT new io.junseok.todeveloperdo.presentation.membertodolist.dto.response.TodoCountResponse(
            t.deadline,
            COUNT(t)
        )
        FROM MemberTodoList t
        WHERE FUNCTION('month', t.deadline) = :month
          AND FUNCTION('year', t.deadline) = :year
          AND t.todoStatus = :status
          AND t.member = :member
        GROUP BY t.deadline
        """
    )
    fun findAllByMonthAndYearAndMember(
        @Param("month") month: Int,
        @Param("year") year: Int,
        @Param("member") member: Member,
        @Param("status") status: TodoStatus = TodoStatus.PROCEED
    ): List<TodoCountResponse>
}