package com.member.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    // 토큰을 블랙리스트에 추가
    public void addToBlacklist(String token, long expirationTime) {

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isBlacklisted(String token) {

        return redisTemplate.hasKey(token);
    }
}
