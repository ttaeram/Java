package com.member.jwt.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDto {

    private Integer memberId;
    private String email;
    private String nickname;
    private String profileUrl;
    private String role;

}
