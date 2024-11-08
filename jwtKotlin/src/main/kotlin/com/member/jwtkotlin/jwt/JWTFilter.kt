package com.member.jwt.jwt

import com.member.jwt.dto.CustomUserDetails
import com.member.jwt.repository.MemberRepository
import com.member.jwt.service.TokenBlacklistService
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
import java.util.function.Supplier

class JWTFilter(
    jwtUtil: JWTUtil,
    private val memberRepository: MemberRepository,
    tokenBlacklistService: TokenBlacklistService
) :
    OncePerRequestFilter() {
    private val jwtUtil: JWTUtil = jwtUtil
    private val tokenBlacklistService: TokenBlacklistService = tokenBlacklistService

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 헤더에서 access에 담긴 토큰 꺼내기
        var accessToken = request.getHeader("Authorization")

        // 토큰이 없는 경우 다음 필터로
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        accessToken = accessToken.substring(7) // "Bearer " 제거

        // 블랙리스트에 있는지 확인
        if (tokenBlacklistService.isBlacklisted(accessToken)) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.print("블랙리스트에 속한 토큰입니다.")
            return
        }

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken)
        } catch (e: ExpiredJwtException) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.print("access token expired")
            return
        }

        // 토큰이 access인지 확인
        val category: String = jwtUtil.getCategory(accessToken)
        if (category != "access") {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.writer.print("invalid access token")
            return
        }

        // email 값 획득 후 사용자 조회
        val email: String = jwtUtil.getEmail(accessToken)
        val memberEntity = memberRepository.findByEmail(email)
            ?.orElseThrow(Supplier { IllegalArgumentException("사용자를 찾을 수 없습니다.") })!!

        // 사용자 권한 설정
        val customUserDetails = CustomUserDetails(memberEntity)
        val authToken: Authentication =
            UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.authorities)
        SecurityContextHolder.getContext().authentication = authToken

        filterChain.doFilter(request, response)
    }
}