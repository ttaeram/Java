package com.member.jwtkotlin.controller

import com.member.jwtkotlin.entity.RefreshEntity
import com.member.jwtkotlin.jwt.JWTUtil
import com.member.jwtkotlin.repository.RefreshRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.util.*

@Controller
@ResponseBody
class ReissueController(private val jwtUtil: JWTUtil, private val refreshRepository: RefreshRepository) {
    @PostMapping("/reissue")
    fun reissue(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<*> {
        // get refresh token

        var refresh: String? = null
        val cookies = request.cookies
        for (cookie in cookies) {
            if (cookie.name == "refresh") {
                refresh = cookie.value
            }
        }

        if (refresh == null) {
            // refresh status code

            return ResponseEntity("refresh token null", HttpStatus.BAD_REQUEST)
        }

        // 만료 체크
        try {
            jwtUtil.isExpired(refresh)
        } catch (e: ExpiredJwtException) {
            // response status code

            return ResponseEntity("refresh token expired", HttpStatus.BAD_REQUEST)
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        val category = jwtUtil.getCategory(refresh)

        if (category != "refresh") {
            // refresh status code

            return ResponseEntity("refresh token invalid", HttpStatus.BAD_REQUEST)
        }

        // DB에 저장되어 있는지 확인
        val isExist = refreshRepository.existsByRefresh(refresh)
        if (!isExist!!) {
            // response body

            return ResponseEntity("refresh token invalid", HttpStatus.BAD_REQUEST)
        }

        val email = jwtUtil.getEmail(refresh)
        val role = jwtUtil.getRole(refresh)

        // JWT 재발급
        val newAccess = jwtUtil.createJwt("access", email, role, 600000L)
        val newRefresh = jwtUtil.createJwt("refresh", email, role, 86400000L)

        // refresh 토큰 저장 DB에 기존의 refresh 토큰 삭제 후 새 refresh 토큰 저장
        refreshRepository.deleteByEmail(email)
        addRefreshEntity(email, newRefresh, 86400000L)

        // response
        response.setHeader("access", newAccess)
        response.addCookie(createCookie("refresh", newRefresh))

        return ResponseEntity<Any>(HttpStatus.OK)
    }

    private fun createCookie(key: String, value: String): Cookie {
        val cookie = Cookie(key, value)
        cookie.maxAge = 24 * 60 * 60
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.isHttpOnly = true

        return cookie
    }

    private fun addRefreshEntity(email: String, newRefresh: String, expiredMs: Long) {
        val date = Date(System.currentTimeMillis() + expiredMs)

        val refreshEntity = RefreshEntity()
        refreshEntity.refresh = newRefresh
        refreshEntity.email = email
        refreshEntity.expiration = date.toString()

        refreshRepository.save(refreshEntity)
    }
}