package io.junseok.todeveloperdo.oauth.dto.response

data class GitUserResponse(
    val username: String,
    val nickname: String,
    val avatarUrl: String,
    val gitUrl: String
){

}
