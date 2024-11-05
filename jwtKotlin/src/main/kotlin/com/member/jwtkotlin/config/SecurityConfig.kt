package com.member.jwtkotlin.config

import com.member.jwtkotlin.jwt.CustomLogoutFilter
import com.member.jwtkotlin.jwt.JWTFilter
import com.member.jwtkotlin.jwt.JWTUtil
import com.member.jwtkotlin.jwt.LoginFilter
import com.member.jwtkotlin.repository.RefreshRepository
import com.member.jwtkotlin.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository,
    private val userRepository: UserRepository // UserRepository 주입 추가
) {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login", "/", "/join", "/reissue").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(JWTFilter(jwtUtil, userRepository), LoginFilter::class.java) // UserRepository 전달
            .addFilterAt(
                LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterBefore(CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter::class.java)
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .cors {
                it.configurationSource {
                    CorsConfiguration().apply {
                        allowedOrigins = listOf("http://localhost:3000")
                        allowedMethods = listOf("*")
                        allowCredentials = true
                        allowedHeaders = listOf("*")
                        maxAge = 3600L
                        exposedHeaders = listOf("Authorization")
                    }
                }
            }

        return http.build()
    }
}
