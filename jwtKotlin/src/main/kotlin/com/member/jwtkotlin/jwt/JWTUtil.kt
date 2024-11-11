package com.member.jwtkotlin.jwt

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JWTUtil(@Value("\${spring.jwt.secret}") secret: String) {
    private val secretKey: SecretKey =
        SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().algorithm)

    fun getEmail(token: String?): String {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.get(
            "email",
            String::class.java
        )
    }

    fun getRole(token: String?): String {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.get(
            "role",
            String::class.java
        )
    }

    fun getCategory(token: String?): String {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.get(
            "category",
            String::class.java
        )
    }

    fun isExpired(token: String?): Boolean {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).payload.expiration.before(Date())
    }

    fun getExpiration(token: String?): Date {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
            .expiration
    }

    fun createJwt(category: String?, email: String?, role: String?, expiredMs: Long): String {
        return Jwts.builder()
            .claim("category", category)
            .claim("email", email)
            .claim("role", role)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiredMs))
            .signWith(secretKey)
            .compact()
    }
}