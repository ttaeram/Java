package com.member.jwt.jwt;

import com.member.jwt.dto.CustomUserDetails;
import com.member.jwt.entity.MemberEntity;
import com.member.jwt.repository.MemberRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.PrintWriter;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JWTFilter(JWTUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 access에 담긴 토큰 꺼내기
        String accessToken = request.getHeader("Authorization");

        // 토큰이 없는 경우 다음 필터로
        if (accessToken == null || !accessToken.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        accessToken = accessToken.substring(7); // "Bearer " 제거

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("access token expired");
            return;
        }

        // 토큰이 access인지 확인
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("invalid access token");
            return;
        }

        // email 값 획득 후 사용자 조회
        String email = jwtUtil.getEmail(accessToken);
        MemberEntity memberEntity = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 권한 설정
        CustomUserDetails customUserDetails = new CustomUserDetails(memberEntity);
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
