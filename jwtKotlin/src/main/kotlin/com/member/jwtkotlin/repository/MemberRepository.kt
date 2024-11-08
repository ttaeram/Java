package com.member.jwt.repository

import com.member.jwt.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<MemberEntity?, Int?> {
    fun existsByEmail(email: String?): Boolean?

    // username을 받아 DB 테이블에서 회원 조회하는 메서드 작성
    fun findByEmail(email: String?): Optional<MemberEntity?>?
}