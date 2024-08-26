package io.junseok.todeveloperdo.domains.member.persistence.entity;


import io.junseok.todeveloperdo.oauth.git.dto.response.GitUserResponse
import javax.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "apple_id")
    var appleId: String? = null,

    @Column(name = "apple_refresh_token")
    var appleRefreshToken: String? = null,

    @Column(name = "apple_email")
    var appleEmail: String? = null,

    @Column(name = "github_username")
    var gitHubUsername: String? = null,

    @Column(name = "github_token")
    var gitHubToken: String? = null,

    @Column(name = "github_repo")
    var gitHubRepo: String? = null,

    @Column(name = "avatar_url") //프로필 이미지 사진
    var avatarUrl: String? = null,

    @Column(name = "github_url")
    var gitHubUrl: String? = null,

    @Column(name = "client_token")
    var clientToken: String? = null,

    @Enumerated(EnumType.STRING)
    val authority: Authority? = Authority.ROLE_USER
) {
    fun updateGitHubRepo(repoName: String) {
        this.gitHubRepo = repoName
    }

    fun updateGitHubToken(accessToken: String) {
        this.gitHubToken = accessToken
    }

    fun updateGitInfo(
        gitUserResponse: GitUserResponse,
        accessToken: String
    ) {
        this.gitHubUsername = gitUserResponse.username
        this.gitHubToken = accessToken
        this.avatarUrl = gitUserResponse.avatarUrl
        this.gitHubUrl = gitUserResponse.gitUrl
    }

    fun removeRepo() {
        this.gitHubRepo = null
    }
}