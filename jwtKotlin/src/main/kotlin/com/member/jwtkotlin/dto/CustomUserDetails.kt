package com.member.jwtkotlin.dto

import com.member.jwtkotlin.entity.MemberEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(private val memberEntity: MemberEntity) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()
        collection.add(GrantedAuthority { memberEntity.role!! })

        return collection
    }

    override fun getPassword(): String? {
        return memberEntity.password
    }

    override fun getUsername(): String? {
        return memberEntity.email
    }

    val nickname: String?
        get() = memberEntity.nickname

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