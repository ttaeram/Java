package com.member.jwt.jwt

import com.member.jwt.repository.RefreshRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException

class CustomLogoutFilter(jwtUtil: JWTUtil, private val refreshRepository: RefreshRepository) : GenericFilterBean() {
    private val jwtUtil: JWTUtil = jwtUtil

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        doFilter(request as HttpServletRequest, response as HttpServletResponse, chain)
    }

    @Throws(IOException::class, ServletException::class)
    private fun doFilter(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        println("Request URI: " + request.requestURI)
        println("Request Method: " + request.method)
        println("Authorization Header: " + request.getHeader("Authorization"))

        // Logout path and method verification
        val requestURI = request.requestURI
        if (requestURI != "/logout" || request.method != "POST") {
            filterChain.doFilter(request, response)
            return
        }

        println("Logout filter activated for /logout")

        // Get refresh token from cookies
        var refresh: String? = null
        val cookies = request.cookies
        if (cookies != null) {
            for (cookie in cookies) {
                if (cookie.name == "refresh") {
                    refresh = cookie.value
                    break
                }
            }
        }

        // Validate refresh token
        if (refresh == null || !jwtUtil.getCategory(refresh).equals("refresh") || !refreshRepository.existsByRefresh(
                refresh
            )!!
        ) {
            response.status = HttpServletResponse.SC_BAD_REQUEST
            response.writer.write("Invalid refresh token.")
            return
        }

        // Remove refresh token from database
        refreshRepository.deleteByEmail(jwtUtil.getEmail(refresh))

        // Expire the refresh token cookie
        val cookie = Cookie("refresh", null)
        cookie.maxAge = 0
        cookie.path = "/"

        response.addCookie(cookie)
        response.status = HttpServletResponse.SC_OK
        response.writer.write("Logged out successfully.")
    }
}