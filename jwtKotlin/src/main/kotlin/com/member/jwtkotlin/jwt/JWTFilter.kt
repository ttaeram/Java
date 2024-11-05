package com.member.jwtkotlin.jwt

import com.member.jwtkotlin.dto.CustomUserDetails
import com.member.jwtkotlin.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JWTFilter(
    private val jwtUtil: JWTUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            println("No valid token provided")
            filterChain.doFilter(request, response)
            return
        }

        val accessToken = authorizationHeader.substring(7) // "Bearer " 제거
        println("Access token: $accessToken")

        // 나머지 코드 유지
        try {
            jwtUtil.isExpired(accessToken)
        } catch (e: ExpiredJwtException) {
            response.writer.print("access token expired")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        val category = jwtUtil.getCategory(accessToken)
        if (category != "access") {
            response.writer.print("invalid access token")
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            return
        }

        val username = jwtUtil.getUsername(accessToken)
        val userEntity = userRepository.findByUsername(username)
            ?: run {
                response.writer.print("user not found")
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                return
            }

        val customUserDetails = CustomUserDetails(userEntity)
        val authToken: Authentication = UsernamePasswordAuthenticationToken(
            customUserDetails, null, customUserDetails.authorities
        )
        SecurityContextHolder.getContext().authentication = authToken
        println("Authentication set in SecurityContextHolder")

        filterChain.doFilter(request, response)
    }
}
