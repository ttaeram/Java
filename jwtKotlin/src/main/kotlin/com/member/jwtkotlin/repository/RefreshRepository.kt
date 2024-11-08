package com.member.jwt.repository

import com.member.jwtkotlin.entity.RefreshEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RefreshRepository : JpaRepository<RefreshEntity?, Long?> {
    fun existsByRefresh(refresh: String?): Boolean?

    // 이메일로 리프레시 토큰을 조회
    fun findByEmail(email: String?): Optional<RefreshEntity?>?

    // 이메일로 리프레시 토큰을 삭제
    @Transactional
    fun deleteByEmail(email: String?)
}