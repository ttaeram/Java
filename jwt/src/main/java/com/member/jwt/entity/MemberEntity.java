package com.member.jwt.entity;

import com.member.jwt.dto.SignupRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "profileUrl")
    private String profileUrl;

    @Column(name = "role", nullable = false)
    private String role;

    @Builder
    public MemberEntity(Integer memberId, String email, String password, String nickname, String profileUrl, String role) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.role = role;
    }

    public static MemberEntity of(SignupRequestDto requestDto, String encodedPassword, String profileUrl) {

        return MemberEntity.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .profileUrl(profileUrl)
                .role("USER")
                .build();
    }
}
