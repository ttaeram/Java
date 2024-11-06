package com.member.jwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    @Email(message = "올바른 형식의 이메일 주소를 입력해주세요.")
    @NotEmpty(message = "이메일을 입력해주세요.")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)[a-z\\d]{1,20}$",
            message = "비밀번호는 영문 소문자, 숫자를 조합하여 20자 이내여야 합니다."
    )
    private String password;
    private String nickname;
    private String profileUrl;
}
