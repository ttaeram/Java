package com.member.jwtkotlin.dto

import org.springframework.web.multipart.MultipartFile

data class MemberUpdateDto(
    val password: String? = null,
    val nickname: String? = null,
    val profileImage: MultipartFile? = null
)
