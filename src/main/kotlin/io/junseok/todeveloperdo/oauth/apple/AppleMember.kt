package io.junseok.todeveloperdo.oauth.apple

import javax.persistence.*

@Entity
@Table(name = "apple_member")
class AppleMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    var id: Long? = null,

    @Column(name = "apple_id")
    var appleId: String? = null,

    @Column(name = "apple_refresh_token")
    var appleRefreshToken: String? = null,

    @Column(name = "github_username")
    var githubUsername: String? = null,

    @Column(name = "github_token")
    var githubToken: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "name")
    var name: String? = null
)
