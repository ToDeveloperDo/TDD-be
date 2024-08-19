package io.junseok.todeveloperdo.domains.gitissue.persistence.entity

import com.fasterxml.jackson.annotation.JsonFormat
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import io.junseok.todeveloperdo.domains.todo.persistence.entity.MemberTodoList
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
    val content: String,

    @Column(name = "memo")
    val memo: String,

    @Column(name = "tag")
    val tag: String,

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
    fun updateDeadline(deadline: LocalDate) {
        this.deadline = deadline
    }

}