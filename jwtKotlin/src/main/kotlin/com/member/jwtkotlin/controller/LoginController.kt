package com.member.jwt.controller

import com.member.jwt.dto.LoginResponseDto
import com.member.jwt.service.LoginService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/login")
class LoginController(private val loginService: LoginService) {
    // 로그인 엔드포인트
    @PostMapping
    fun login(@RequestParam email: String?, @RequestParam password: String?): ResponseEntity<LoginResponseDto> {
        val loginResponseDto = loginService.login(email, password)
        return ResponseEntity.ok(loginResponseDto)
    }

    // 예외 처리 핸들러 추가
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<String> {
        return ResponseEntity.status(ex.statusCode).body(ex.reason)
    }
}