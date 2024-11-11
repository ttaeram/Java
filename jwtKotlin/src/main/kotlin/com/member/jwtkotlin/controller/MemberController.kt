package com.member.jwtkotlin.controller

import com.member.jwtkotlin.dto.MemberInfoDto
import com.member.jwtkotlin.dto.MemberUpdateDto
import com.member.jwtkotlin.service.MemberService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class MemberController(private val memberService: MemberService) {
    @GetMapping
    fun getMemberInfo(@RequestParam memberId: Int?): ResponseEntity<MemberInfoDto> {
        val memberInfo = memberService.getMemberInfo(memberId!!)
        return ResponseEntity.ok(memberInfo)
    }

    @PatchMapping
    fun updateMemberInfo(
        @RequestParam memberId: Int?,
        @ModelAttribute memberUpdateDto: MemberUpdateDto?
    ): ResponseEntity<MemberInfoDto> {
        val updatedMemberInfo = memberService.updateMemberInfo(memberId!!, memberUpdateDto!!)
        return ResponseEntity.ok(updatedMemberInfo)
    }

    @DeleteMapping
    fun deleteMember(@RequestParam memberId: Int?): ResponseEntity<String> {
        memberService.deleteMemberInfo(memberId!!)
        return ResponseEntity.ok("회원 삭제 완료")
    }
}