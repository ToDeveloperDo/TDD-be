package io.junseok.todeveloperdo.domains

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseTimeEntity(
    @CreationTimestamp
    @Column(name = "create_dt")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    var createDt: LocalDateTime?=null,

/*    @UpdateTimestamp
    @Column(name = "modify_dt")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    var modifyDt: LocalDateTime*/
)