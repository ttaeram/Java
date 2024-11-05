package com.member.jwtkotlin.controller

import com.member.jwtkotlin.dto.JoinDTO
import com.member.jwtkotlin.service.JoinService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@ResponseBody
class JoinController(joinService: JoinService) {
    private val joinService: JoinService = joinService

    @PostMapping("/join")
    fun joinProcess(joinDTO: JoinDTO): String {
        System.out.println(joinDTO.username)
        joinService.joinProcess(joinDTO)

        return "ok"
    }
}