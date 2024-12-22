package io.junseok.todeveloperdo.domains.curriculum.content.persistence.entity

import io.junseok.todeveloperdo.domains.curriculum.persistence.entity.Curriculum
import javax.persistence.*

@Table(name = "content")
@Entity
class Content(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    var contentId: Long? = null,

    @Column(name = "learn_content")
    val learnContent: String,

    @Column(name = "is_checked")
    val isChecked: Boolean,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    val curriculum: Curriculum
) {
}