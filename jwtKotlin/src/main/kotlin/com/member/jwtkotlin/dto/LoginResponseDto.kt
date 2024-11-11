package com.member.jwtkotlin.dto

data class LoginResponseDto(
    val accessToken: String?,
    val refreshToken: String?,
    val userInfo: MemberInfoDto?
)
