package io.junseok.todeveloperdo.domains.curriculum.plan.persistence.entity

import io.junseok.todeveloperdo.domains.BaseTimeEntity
import io.junseok.todeveloperdo.domains.member.persistence.entity.Member
import javax.persistence.*

@Table(name = "curriculum_plan")
@Entity
class CurriculumPlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    var planId: Long? = null,

    @Column(name = "position")
    val position: String,

    @Column(name = "stack")
    val stack: String,

    @Column(name = "experience_level")
    val experienceLevel: String,

    @Column(name = "target_period")
    val targetPeriod: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member
) : BaseTimeEntity(){

}