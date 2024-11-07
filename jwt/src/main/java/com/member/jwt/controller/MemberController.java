package com.member.jwt.controller;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.service.LoginService;
import com.member.jwt.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {

        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<MemberInfoDto> getMemberInfo(@RequestParam Integer memberId) {
        MemberInfoDto memberInfo = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(memberInfo);
    }

    @PutMapping
    public ResponseEntity<MemberInfoDto> updateMemberInfo(
            @RequestParam Integer memberId,
            @RequestBody MemberInfoDto memberInfoDto) {

        MemberInfoDto updatedMemberInfo = memberService.updateMemberInfo(memberId, memberInfoDto);
        return ResponseEntity.ok(updatedMemberInfo);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMember(@RequestParam Integer memberId) {
        memberService.deleteMemberInfo(memberId);
        return ResponseEntity.ok("회원 삭제 완료");
    }

    // 예외 처리 핸들러 추가
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
}

