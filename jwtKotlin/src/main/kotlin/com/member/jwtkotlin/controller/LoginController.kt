package com.member.jwtkotlin.controller

import com.member.jwtkotlin.dto.LoginResponseDto
import com.member.jwtkotlin.service.LoginService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/login")
class LoginController(private val loginService: LoginService) {
    @PostMapping
    fun login(@RequestParam email: String?, @RequestParam password: String?): ResponseEntity<LoginResponseDto> {
        try {
            val response = loginService.login(email, password)
            return ResponseEntity.ok(response)
        } catch (ex: BadCredentialsException) {
            // 특정 인증 실패 예외를 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null)
        }
    }
}