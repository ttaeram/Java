package com.member.jwt.dto

data class LoginResponseDto(
    val accessToken: String?,
    val refreshToken: String?,
    val userInfo: MemberInfoDto?
)
