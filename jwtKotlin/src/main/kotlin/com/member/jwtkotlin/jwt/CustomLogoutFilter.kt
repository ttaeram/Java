package com.member.jwtkotlin.jwt

import com.member.jwtkotlin.repository.RefreshRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

class CustomLogoutFilter(private val jwtUtil: JWTUtil, refreshRepository: RefreshRepository) : GenericFilterBean() {
    private val refreshRepository: RefreshRepository = refreshRepository

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    @Throws(IOException::class, ServletException::class)
    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        // path and method verify

        val requestURI = request.requestURI
        if (!requestURI.matches("^\\\\/logout$".toRegex())) {
            filterChain.doFilter(request, response)
            return
        }

        val requestMethod = request.method
        if (requestMethod != "POST") {
            filterChain.doFilter(request, response)
            return
        }

        // get frefresh token
        var refresh: String? = null
        val cookies = request.cookies
        for (cookie in cookies) {
            if (cookie.name == "refresh") {
                refresh = cookie.value
            }
        }

        // refresh null check
        if (refresh == null) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
        val category = jwtUtil.getCategory(refresh)
        if (category != "refresh") {
            // refresh status code

            response.status = HttpServletResponse.SC_BAD_REQUEST
            return
        }

        // DB에 저장되어 있는지 확인
        val isExist: Boolean = refreshRepository.existsByRefresh(refresh)
        if (!isExist) {
            // refresh status code

            response.status = HttpServletResponse.SC_BAD_REQUEST
        }

        // 로그아웃 진행, refresh token DB에서 제거
        refreshRepository.deleteByRefresh(refresh)

        // refresh 토큰 cookie 값 0
        val cookie = Cookie("refresh", null)
        cookie.maxAge = 0
        cookie.path = "/"

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_OK
    }
}