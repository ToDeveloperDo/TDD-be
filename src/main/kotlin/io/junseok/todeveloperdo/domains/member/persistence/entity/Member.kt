package io.junseok.todeveloperdo.domains.member.persistence.entity;


import javax.persistence.*

@Entity
@Table(name = "member")
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var memberId: Long? = null,

    @Column(name = "username") // 로그인 시 사용
    var username: String,

    @Column(name = "nickname")
    var nickname: String,

    @Column(name = "avatar_url") //프로필 이미지 사진
    var avatarUrl: String,

    @Column(name = "git_url")
    var gitUrl: String,

    @Column(name = "github_token")
    var gitHubToken: String,

    @Column(name = "github_repo")
    var gitHubRepo: String? = "",

    @Column(name = "activated")
    var activated: Boolean,

    @Enumerated(EnumType.STRING)
    val authority: Authority
) {
    fun updateGitHubRepo(repoName: String) {
        this.gitHubRepo = repoName
    }

    fun updateGitHubToken(accessToken: String){
        this.gitHubToken=accessToken
    }
}