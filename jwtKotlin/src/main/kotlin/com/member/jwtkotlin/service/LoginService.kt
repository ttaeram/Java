package com.member.jwtkotlin.service

import com.member.jwtkotlin.dto.LoginResponseDto
import com.member.jwtkotlin.dto.MemberInfoDto
import com.member.jwtkotlin.entity.RefreshEntity
import com.member.jwtkotlin.jwt.JWTUtil
import com.member.jwtkotlin.repository.MemberRepository
import com.member.jwtkotlin.repository.RefreshRepository
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import java.util.function.Supplier

@Service
class LoginService(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository,
    private val memberRepository: MemberRepository
) {
    fun login(email: String?, password: String?): LoginResponseDto {
        val memberEntity = memberRepository.findByEmail(email)
            ?.orElseThrow(Supplier { ResponseStatusException(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다.") })!!

        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(email, password)
            )

            val role = authentication.authorities.iterator().next().authority
            val accessToken = jwtUtil.createJwt("access", email, role, 600000L)

            // 기존 유효한 리프레시 토큰이 있는지 확인
            val refreshToken = getOrCreateRefreshToken(email, role)

            val memberInfo = MemberInfoDto(
                memberEntity.memberId,
                memberEntity.email,
                memberEntity.nickname,
                memberEntity.profileUrl,
                memberEntity.role
            )
            return LoginResponseDto(accessToken, refreshToken, memberInfo)
        } catch (e: BadCredentialsException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.")
        }
    }

    private fun getOrCreateRefreshToken(email: String?, role: String): String? {
        // 유효한 리프레시 토큰이 있으면 사용, 없으면 새로 생성
        val existingRefresh: Optional<RefreshEntity?>? = refreshRepository.findByEmail(email)
        if (existingRefresh!!.isPresent && !jwtUtil.isExpired(existingRefresh.get().refresh)) {
            return existingRefresh.get().refresh
        }

        // 기존 토큰이 없거나 만료된 경우 새로 생성
        val newRefreshToken = jwtUtil.createJwt("refresh", email, role, 86400000L)
        addRefreshEntity(email, newRefreshToken, 86400000L)
        return newRefreshToken
    }

    private fun addRefreshEntity(email: String?, refresh: String, expiredMs: Long) {
        val expirationDate = Date(System.currentTimeMillis() + expiredMs)

        // 기존의 리프레시 토큰이 있으면 삭제
        refreshRepository.deleteByEmail(email)

        val refreshEntity = RefreshEntity()
        refreshEntity.refresh = refresh
        refreshEntity.email = email
        refreshEntity.expiration = expirationDate.toString()
        refreshRepository.save(refreshEntity)
    }
}