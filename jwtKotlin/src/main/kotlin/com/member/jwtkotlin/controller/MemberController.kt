package com.member.jwt.controller

import com.member.jwt.dto.MemberInfoDto
import com.member.jwt.dto.MemberUpdateDto
import com.member.jwt.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class MemberController(private val memberService: MemberService) {
    @GetMapping
    fun getMemberInfo(@RequestParam memberId: Int): ResponseEntity<MemberInfoDto> {
        val memberInfo = memberService.getMemberInfo(memberId)
        return ResponseEntity.ok(memberInfo)
    }

    @PatchMapping
    fun updateMemberInfo(
        @RequestParam memberId: Int,
        @ModelAttribute memberUpdateDto: MemberUpdateDto
    ): ResponseEntity<MemberInfoDto> {
        val updatedMemberInfo = memberService.updateMemberInfo(memberId, memberUpdateDto)
        return ResponseEntity.ok(updatedMemberInfo)
    }

    @DeleteMapping
    fun deleteMember(@RequestParam memberId: Int): ResponseEntity<String> {
        memberService.deleteMemberInfo(memberId)
        return ResponseEntity.ok("회원 삭제 완료")
    }

    // 예외 처리 핸들러 추가
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<String> {
        return ResponseEntity.status(ex.statusCode).body(ex.reason)
    }
}