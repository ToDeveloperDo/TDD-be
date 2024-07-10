package io.junseok.todeveloperdo.domains.todo.persistence.entity

import com.fasterxml.jackson.annotation.JsonFormat
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "member_todo_list")
class MemberTodoList(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_list_id")
    var todoListId: Long? = null,

    @Column(name = "content")
    var content: String,

    @Column(name = "memo")
    var memo: String?="",

    @Column(name = "tag")
    var tag: String,

    @Column(name = "deadline")
    @JsonFormat(pattern = "yyyy-MM-dd")
    var deadline: LocalDate,

    @Column(name = "is_share")
    var isShare:Boolean,

    @Column(name = "issue_number")
    var issueNumber: Int?= null,

    @Enumerated(EnumType.STRING)
    var todoStatus: TodoStatus,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
){
    fun updateTodoStatus(){
        this.todoStatus = TodoStatus.DONE
    }
}
