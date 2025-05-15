package io.junseok.todeveloperdo.domains.curriculum.persistence.entity

import io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity.CurriculumPlan
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import javax.persistence.*

@Table(name = "curriculum")
@Entity
class Curriculum(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curriculum_id")
    var curriculumId: Long?= null,

   @Column(name = "week_title")
    val weekTitle: String,

    @Column(name = "objective")
    val objective: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    val curriculumPlan: CurriculumPlan
)