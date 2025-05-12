package io.junseok.todeveloperdo.oauth.git.service.loginserviceimpl

import io.junseok.todeveloperdo.domains.member.service.serviceimpl.MemberReader
import io.junseok.todeveloperdo.domains.member.service.serviceimpl.createMember
import io.junseok.todeveloperdo.exception.ErrorCode.NOT_LINK_GITHUB
import io.junseok.todeveloperdo.exception.ToDeveloperDoException
import io.junseok.todeveloperdo.util.throwsWith
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GitLinkValidatorTest : FunSpec({
    val memberReader = mockk<MemberReader>()
    val gitLinkValidator = GitLinkValidator(memberReader)

    test("깃허브가 연동되어 있다면 예외가 발생하지 않는다.") {
        val member = createMember(1, "appleId")
        every { memberReader.getMember(any()) } returns member
        shouldNotThrow<ToDeveloperDoException> {
            gitLinkValidator.isGitLink(member.appleId!!)
        }
    }

    test("깃허브가 연동되어 있지 않다면 NOT_LINK_GITHUB에러를 반환해야한다.") {
        val member = createMember(1, "appleId")
        every { memberReader.getMember(any()) } returns member
        member.gitHubUsername = null
        throwsWith<ToDeveloperDoException>(
            {
                gitLinkValidator.isGitLink(member.appleId!!)
            },
            {
                ex -> ex.errorCode shouldBe NOT_LINK_GITHUB
            }
        )
    }


})
