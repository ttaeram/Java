package com.member.jwt.service;

import com.member.jwt.dto.LoginResponseDto;
import com.member.jwt.dto.MemberInfoDto;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.entity.RefreshEntity;
import com.member.jwt.jwt.JWTUtil;
import com.member.jwt.repository.MemberRepository;
import com.member.jwt.repository.RefreshRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

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

    public LoginResponseDto login(String email, String password) {
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."));

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            String role = authentication.getAuthorities().iterator().next().getAuthority();
            String accessToken = jwtUtil.createJwt("access", email, role, 600000L);

            // 기존 유효한 리프레시 토큰이 있는지 확인
            String refreshToken = getOrCreateRefreshToken(email, role);

            MemberInfoDto memberInfo = new MemberInfoDto(
                    memberEntity.getMemberId(),
                    memberEntity.getEmail(),
                    memberEntity.getNickname(),
                    memberEntity.getProfileUrl(),
                    memberEntity.getRole()
            );
            return new LoginResponseDto(accessToken, refreshToken, memberInfo);

        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다.");
        }
    }

    private String getOrCreateRefreshToken(String email, String role) {
        // 유효한 리프레시 토큰이 있으면 사용, 없으면 새로 생성
        Optional<RefreshEntity> existingRefresh = refreshRepository.findByEmail(email);
        if (existingRefresh.isPresent() && !jwtUtil.isExpired(existingRefresh.get().getRefresh())) {
            return existingRefresh.get().getRefresh();
        }

        // 기존 토큰이 없거나 만료된 경우 새로 생성
        String newRefreshToken = jwtUtil.createJwt("refresh", email, role, 86400000L);
        addRefreshEntity(email, newRefreshToken, 86400000L);
        return newRefreshToken;
    }

    private void addRefreshEntity(String email, String refresh, long expiredMs) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredMs);

        // 기존의 리프레시 토큰이 있으면 삭제
        refreshRepository.deleteByEmail(email);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setRefresh(refresh);
        refreshEntity.setEmail(email);
        refreshEntity.setExpiration(expirationDate.toString());
        refreshRepository.save(refreshEntity);
    }
}
