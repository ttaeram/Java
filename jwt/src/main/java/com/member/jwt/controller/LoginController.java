package com.member.jwt.controller;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestParam String email, @RequestParam String password) {
        try {
            LoginResponseDto response = loginService.login(email, password);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            // 특정 인증 실패 예외를 처리
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
