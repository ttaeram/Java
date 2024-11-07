package com.member.jwt.controller;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {

        this.loginService = loginService;
    }

    // 로그인 엔드포인트
    @PostMapping
    public ResponseEntity<LoginResponseDto> login(@RequestParam String email, @RequestParam String password) {
        LoginResponseDto loginResponseDto = loginService.login(email, password);
        return ResponseEntity.ok(loginResponseDto);
    }

    // 예외 처리 핸들러 추가
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}
