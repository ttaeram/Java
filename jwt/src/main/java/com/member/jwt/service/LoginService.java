package com.member.jwt.service;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.entity.RefreshEntity;
import com.member.jwt.jwt.JWTUtil;
import com.member.jwt.repository.MemberRepository;
import com.member.jwt.repository.RefreshRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    public LoginService(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository, MemberRepository memberRepository) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.memberRepository = memberRepository;
    }

    public LoginResponseDto login(String email, String password) throws AuthenticationException {

        // AuthenticationManager를 사용하여 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 인증 성공 시 토큰 생성
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String accessToken = jwtUtil.createJwt("access", email, role, 600000L);
        String refreshToken = jwtUtil.createJwt("refresh", email, role, 86400000L);

        // Refresh 토큰을 저장
        addRefreshEntity(email, refreshToken, 86400000L);

        // 사용자 정보 조회
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        MemberInfoDto memberInfo = new MemberInfoDto(
                memberEntity.getMemberId(),
                memberEntity.getEmail(),
                memberEntity.getNickname(),
                memberEntity.getProfileUrl(),
                memberEntity.getRole()
        );

        // 토큰을 담아 응답 DTO 반환
        return new LoginResponseDto(accessToken, refreshToken, memberInfo);
    }

    private void addRefreshEntity(String email, String refresh, long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefresh(refresh);
        refreshEntity.setEmail(email);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }
}
