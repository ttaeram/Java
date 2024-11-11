package com.member.jwtkotlin.service

import com.member.jwtkotlin.dto.CustomUserDetails
import com.member.jwtkotlin.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.function.Supplier

@Service
class CustomMemberDetailService(private val memberRepository: MemberRepository) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val memberEntity = memberRepository.findByEmail(email)
            ?.orElseThrow(Supplier { UsernameNotFoundException("사용자를 찾을 수 없습니다.") })!!

        return CustomUserDetails(memberEntity)
    }
}