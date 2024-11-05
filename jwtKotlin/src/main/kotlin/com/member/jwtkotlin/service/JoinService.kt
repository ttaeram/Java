package com.member.jwtkotlin.service

import com.member.jwtkotlin.dto.JoinDTO
import com.member.jwtkotlin.entity.UserEntity
import com.member.jwtkotlin.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class JoinService(userRepository: UserRepository, private val bCryptPasswordEncoder: BCryptPasswordEncoder) {
    private val userRepository: UserRepository = userRepository

    fun joinProcess(joinDTO: JoinDTO) {
        val username: String = joinDTO.username
        val password: String = joinDTO.password
        val nickname: String = joinDTO.nickname

        val isExist: Boolean = userRepository.existsByUsername(username)

        if (isExist) {
            return
        }

        val data: UserEntity = UserEntity()

        data.username = username
        data.password = bCryptPasswordEncoder.encode(password)
        data.nickname = nickname
        data.role = "ROLE_ADMIN"

        userRepository.save(data)
    }
}