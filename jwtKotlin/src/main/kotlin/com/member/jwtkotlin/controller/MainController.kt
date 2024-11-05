package com.member.jwtkotlin.controller

import com.member.jwtkotlin.dto.CustomUserDetails
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@ResponseBody
class MainController {

    @GetMapping("/")
    fun mainP(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        println("Authentication: $authentication")
        println("Principal: ${authentication.principal}")

        val principal = authentication.principal
        val nickname = if (principal is CustomUserDetails) {
            principal.nickname ?: "Anonymous"
        } else {
            "Anonymous"
        }

        return "main Controller: $nickname"
    }
}
