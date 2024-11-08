package com.member.jwt.controller

import com.member.jwt.dto.SignupRequestDto
import com.member.jwt.dto.SignupResponseDto
import com.member.jwt.service.SignupService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile

@Controller
@ResponseBody
class SignupController(private val signupService: SignupService) {
    @PostMapping("/signup")
    fun signupProcess(
        @ModelAttribute signupRequestDto: @Valid SignupRequestDto,
        @RequestParam("profileImage") profileImage: MultipartFile
    ): ResponseEntity<SignupResponseDto> {
        println(signupRequestDto.email)
        val memberId = signupService.signupProcess(signupRequestDto, profileImage)

        return ResponseEntity.ok(SignupResponseDto(memberId))
    }
}