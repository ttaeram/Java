package com.member.jwtkotlin.config

import com.member.jwtkotlin.jwt.JWTFilter
import com.member.jwtkotlin.jwt.JWTUtil
import com.member.jwtkotlin.repository.MemberRepository
import com.member.jwtkotlin.repository.RefreshRepository
import com.member.jwtkotlin.service.CustomMemberDetailService
import com.member.jwtkotlin.service.TokenBlacklistService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val authenticationConfiguration: AuthenticationConfiguration,
    private val jwtUtil: JWTUtil,
    private val refreshRepository: RefreshRepository,
    private val customMemberDetailService: CustomMemberDetailService,
    private val tokenBlacklistService: TokenBlacklistService
) {
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(customMemberDetailService)
        authProvider.setPasswordEncoder(bCryptPasswordEncoder())
        return authProvider
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(): AuthenticationManager {
        val authenticationManager = authenticationConfiguration.authenticationManager
        (authenticationManager as ProviderManager).providers.add(authenticationProvider())
        return authenticationManager
    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(
        http: HttpSecurity,
        refreshRepository: RefreshRepository?,
        memberRepository: MemberRepository?
    ): SecurityFilterChain {
        http
            .csrf { csrf: CsrfConfigurer<HttpSecurity> -> csrf.disable() }
            .formLogin { form: FormLoginConfigurer<HttpSecurity> -> form.disable() }
            .httpBasic { basic: HttpBasicConfigurer<HttpSecurity> -> basic.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/login", "/", "/signup").permitAll()
                    .requestMatchers("/auth/**", "auth-logout").authenticated()
                    .anyRequest().authenticated()
            }

            .addFilterBefore(
                JWTFilter(jwtUtil, memberRepository!!, tokenBlacklistService),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }
            .cors { cors: CorsConfigurer<HttpSecurity?> ->
                cors.configurationSource {
                    val configuration = CorsConfiguration()
                    configuration.allowedOrigins = listOf("http://localhost:3000")
                    configuration.allowedMethods = listOf("*")
                    configuration.allowCredentials = true
                    configuration.allowedHeaders = listOf("*")
                    configuration.maxAge = 3600L
                    configuration.exposedHeaders = listOf("Authorization")
                    configuration
                }
            }

        return http.build()
    }
}