package com.member.jwt.controller;

import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.dto.MemberUpdateDto;
import com.member.jwt.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping
    public ResponseEntity<MemberInfoDto> updateMemberInfo(
            @RequestParam Integer memberId,
            @ModelAttribute MemberUpdateDto memberUpdateDto) {
        MemberInfoDto updatedMemberInfo = memberService.updateMemberInfo(memberId, memberUpdateDto);
        return ResponseEntity.ok(updatedMemberInfo);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMember(@RequestParam Integer memberId) {
        memberService.deleteMemberInfo(memberId);
        return ResponseEntity.ok("회원 삭제 완료");
    }
}
