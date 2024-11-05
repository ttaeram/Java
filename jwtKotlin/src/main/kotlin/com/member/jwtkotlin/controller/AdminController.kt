package com.member.jwtkotlin.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody


@Controller
@ResponseBody
class AdminController {
    @GetMapping("/admin")
    fun adminP(): String {
        return "admin Controller"
    }
}
