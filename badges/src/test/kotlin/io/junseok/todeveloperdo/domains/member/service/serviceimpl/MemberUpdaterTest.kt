package io.junseok.todeveloperdo.domains.member.service.serviceimpl

import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberUpdaterTest : BehaviorSpec({
    val memberUpdater = MemberUpdater()
    Given("사용자 깃허브 토큰을 업데이트 하는 경우") {
        val accessToken = "modify accessToken"
        val member = createMember(1L, "test", "test")
        When("정상적인 액세스 토큰이 들어온 경우") {
            memberUpdater.updateMemberToken(accessToken, member)
            Then("정상적으로 변경이 되어야한다.") {
                member.gitHubToken shouldBe accessToken
            }
        }
    }

    Given("사용자 깃허브 레포르 업데이트 하는 경우") {
        val repoName = "modify RepoName"
        val member = createMember(1L, "test", "test")
        When("정상적인 레포 명이 들어온 경우") {
            memberUpdater.updateMemberRepo(repoName, member)
            Then("정상적으로 변경이 되어야 한다.") {
                member.gitHubRepo shouldBe repoName
            }
        }
    }

    Given("사용자가 회원탈퇴를 하는 경우") {
        val member = createMember(1L, "test", "test")
        When("레포가 정상적으로 삭제가 되면") {
            memberUpdater.removeMemberRepo(member)
        }
        Then("레포가 Null이 되어야 한다.") {
            member.gitHubRepo shouldBe null
        }
    }

    Given("사용자 깃허브 정보를 업데이트하는 경우") {
        val member = createMember(1L, "test", "test")
        val accessToken = "access Token"
        val gitUserResponse = GitUserResponse(
            username = "modify username",
            avatarUrl = "modify avatarUrl",
            gitUrl = "modify gitUrl"
        )
        When("updateGitMemberInfo가 호출되면") {
            memberUpdater.updateGitMemberInfo(gitUserResponse, accessToken, member)
            Then("정상적으로 정보가 업데이트 되어야 한다.") {
                member.gitHubUsername shouldBe "modify username"
                member.gitHubToken shouldBe accessToken
                member.avatarUrl shouldBe "modify avatarUrl"
                member.gitHubUrl shouldBe "modify gitUrl"
            }
        }
    }

    Given("사용자 FCM Token을 업데이트 하는 경우") {
        val fcmToken = "modify fcmToken"
        val member = createMember(1L, "test", "test")
        When("updateFcmToken가 호출되면") {
            memberUpdater.updateFcmToken(fcmToken, member)
            Then("정상적으로 업데이트가 되어야한다.") {
                member.clientToken shouldBe fcmToken
            }
        }
    }
})
