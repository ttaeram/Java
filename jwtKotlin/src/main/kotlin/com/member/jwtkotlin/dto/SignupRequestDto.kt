package com.member.jwtkotlin.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Pattern

data class SignupRequestDto(
    @field:Email(message = "올바른 형식의 이메일 주소를 입력해주세요.")
    @field:NotEmpty(message = "이메일을 입력해주세요.")
    val email: String? = null,

    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{1,20}$",
        message = "비밀번호는 영문 소문자, 숫자를 조합하여 20자 이내여야 합니다."
    )
    val password: String? = null,

    @field:NotEmpty(message = "닉네임을 입력해주세요.")
    val nickname: String? = null,

    val profileUrl: String? = null
)
