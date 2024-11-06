package com.member.jwt.controller;

import com.member.jwt.dto.SignupRequestDto;
import com.member.jwt.dto.SignupResponseDto;
import com.member.jwt.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@ResponseBody
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {

        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signupProcess(
            @Valid @ModelAttribute SignupRequestDto signupRequestDto,
            @RequestParam("profileImage") MultipartFile profileImage) {

        System.out.println(signupRequestDto.getEmail());
        Integer memberId = signupService.signupProcess(signupRequestDto, profileImage);

        return ResponseEntity.ok(new SignupResponseDto(memberId));
    }
}
