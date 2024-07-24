package io.junseok.todeveloperdo.event.readme.dto

import io.junseok.todeveloperdo.domains.member.persistence.entity.Member

data class ReadMeEventRequest(val member: Member)
