package com.member.jwtkotlin.dto

import com.member.jwtkotlin.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(userEntity: UserEntity) : UserDetails {
    private val userEntity: UserEntity = userEntity

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { userEntity.role })

        return collection
    }

    override fun getPassword(): String? {
        return userEntity.password
    }

    override fun getUsername(): String? {
        return userEntity.username
    }

    val nickname: String?
        get() = userEntity.nickname

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}