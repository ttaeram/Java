package com.member.jwt.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TokenBlacklistService @Autowired constructor(private val redisTemplate: RedisTemplate<String, String>) {
    // 토큰을 블랙리스트에 추가
    fun addToBlacklist(token: String, expirationTime: Long) {
        val valueOperations = redisTemplate.opsForValue()
        valueOperations[token, "blacklisted", expirationTime] = TimeUnit.MILLISECONDS
    }

    // 토큰이 블랙리스트에 있는지 확인
    fun isBlacklisted(token: String): Boolean {
        return redisTemplate.hasKey(token)
    }
}