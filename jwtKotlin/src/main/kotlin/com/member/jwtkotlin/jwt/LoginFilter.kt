package com.member.jwtkotlin.jwt

import com.member.jwtkotlin.entity.RefreshEntity
import com.member.jwtkotlin.repository.RefreshRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*

class LoginFilter(
    private val authenticationManager: AuthenticationManager, // JWTUtil 주입
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository
) :
    UsernamePasswordAuthenticationFilter() {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        // 클라이언트 요청에서 username, password 추출

        val username = obtainUsername(request)
        val password = obtainPassword(request)

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서 token에 담아야 함
        val authToken = UsernamePasswordAuthenticationToken(username, password, null)

        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        // 유저 정보

        val username = authentication.name

        val authorities = authentication.authorities
        val iterator: Iterator<GrantedAuthority> = authorities.iterator()
        val auth = iterator.next()
        val role = auth.authority

        // 토큰 생성
        val access = jwtUtil.createJwt("access", username, role, 600000L)
        val refresh = jwtUtil.createJwt("refresh", username, role, 86400000L)

        // Refresh 토큰 저장
        addRefreshEntity(username, refresh, 86400000L)

        // 응답 설정
        response.setHeader("access", access)
        response.addCookie(createCookie("refresh", refresh))
        response.status = HttpStatus.OK.value()
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        // 로그인 실패 시 401 응답 코드 반환

        response.status = 401
    }

    private fun createCookie(key: String, value: String): Cookie {
        val cookie = Cookie(key, value)
        cookie.maxAge = 24 * 60 * 60
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.isHttpOnly = true

        return cookie
    }

    private fun addRefreshEntity(username: String, refresh: String, expiredMs: Long) {
        val date = Date(System.currentTimeMillis() + expiredMs)

        val refreshEntity: RefreshEntity = RefreshEntity()
        refreshEntity.refresh = refresh
        refreshEntity.username = username
        refreshEntity.expiration = date.toString()

        refreshRepository.save(refreshEntity)
    }
}