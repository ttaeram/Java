package com.member.jwt.controller

import com.member.jwt.jwt.JWTUtil
import com.member.jwt.repository.RefreshRepository
import com.member.jwt.service.TokenBlacklistService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth-logout")
class LogoutController(
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository,
    private val tokenBlacklistService: TokenBlacklistService
) {
    @PostMapping
    fun logout(
        @RequestHeader("Authorization") accessToken: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<String> {
        // Bearer prefix 제거

        var accessToken = accessToken
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7)
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 엑세스 토큰입니다.")
        }

        // 엑세스 토큰을 블랙리스트에 추가
        val expirationDate = jwtUtil.getExpiration(accessToken)
        val timeToExpire = expirationDate.time - System.currentTimeMillis()
        tokenBlacklistService.addToBlacklist(accessToken, timeToExpire)

        // 쿠키에서 refresh 토큰 추출
        var refreshToken: String? = null
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if ("refresh" == cookie.name) {
                    refreshToken = cookie.value
                    break
                }
            }
        }

        if (refreshToken == null || jwtUtil.getCategory(refreshToken) != "refresh") {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 리프레시 토큰입니다.")
        }

        // refresh 토큰이 데이터베이스에 있는지 확인
        if (!refreshRepository.existsByRefresh(refreshToken)!!) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 리프레시 토큰입니다.")
        }

        // DB에서 refresh 토큰 삭제
        refreshRepository.deleteByEmail(jwtUtil.getEmail(refreshToken))

        // 쿠키 만료시켜서 삭제
        val deleteCookie = Cookie("refresh", null)
        deleteCookie.maxAge = 0
        deleteCookie.path = "/"
        response.addCookie(deleteCookie)

        return ResponseEntity.ok("로그아웃 성공")
    }
}