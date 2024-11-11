package com.member.jwtkotlin.controller

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@ResponseBody
class MainController {
    @GetMapping("/")
    fun mainP(): String {
        val name = SecurityContextHolder.getContext().authentication.name

        return "main Controller: $name"
    }
}