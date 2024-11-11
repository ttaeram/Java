package com.member.jwtkotlin.entity

import com.member.jwtkotlin.dto.SignupRequestDto
import jakarta.persistence.*

@Entity
@Table(name = "member")
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    var memberId: Int? = null,

    @Column(name = "email", nullable = false)
    var email: String?,

    @Column(name = "password", nullable = false)
    var password: String?,

    @Column(name = "nickname", nullable = false)
    var nickname: String?,

    @Column(name = "profileUrl")
    var profileUrl: String? = null,

    @Column(name = "role", nullable = false)
    var role: String?
) {
    companion object {
        fun of(requestDto: SignupRequestDto, encodedPassword: String, profileUrl: String?): MemberEntity {
            return MemberEntity(
                email = requestDto.email,
                password = encodedPassword,
                nickname = requestDto.nickname,
                profileUrl = profileUrl,
                role = "USER"
            )
        }
    }
}
