package com.member.jwt.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private MemberInfoDto userInfo;
}
