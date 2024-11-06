package com.member.jwt.controller;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {

        this.loginService = loginService;
    }

    @PostMapping("/auth")
    public ResponseEntity<LoginResponseDto> login(@RequestParam String email, @RequestParam String password) {

        LoginResponseDto loginResponseDto = loginService.login(email, password);
        return ResponseEntity.ok(loginResponseDto);
    }
}
