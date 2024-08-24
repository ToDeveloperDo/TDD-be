package io.junseok.todeveloperdo.domains.gitissue.persistence.entity

import com.fasterxml.jackson.annotation.JsonFormat
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
import io.junseok.todeveloperdo.presentation.membertodolist.dto.request.TodoRequest
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "git_issue")
class GitIssue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    var issueId: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "memo")
    var memo: String,

    @Column(name = "tag")
    var tag: String,

    @Column(name = "deadline")
    @JsonFormat(pattern = "yyyy-MM-dd")
    var deadline: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_list_id")
    val todoList: MemberTodoList
) {
    fun update(todoRequest: TodoRequest) {
        this.content = todoRequest.content
        this.memo = todoRequest.memo!!
        this.tag =todoRequest.tag
        this.deadline = todoRequest.deadline
    }
}