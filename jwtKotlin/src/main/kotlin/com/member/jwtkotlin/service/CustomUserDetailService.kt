package com.member.jwtkotlin.service

import com.member.jwtkotlin.dto.CustomUserDetails
import com.member.jwtkotlin.entity.UserEntity
import com.member.jwtkotlin.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(private val userRepository: UserRepository) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        // DB에서 사용자 조회 및 null 체크
        val userData: UserEntity = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User with username $username not found")

        // UserDetails에 담아서 return 하면 AuthenticationManager가 검증
        return CustomUserDetails(userData)
    }
}
