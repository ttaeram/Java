package com.member.jwtkotlin.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:Email(message = "올바른 형식의 이메일 주소를 입력해주세요.")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String? = null,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String? = null
)
