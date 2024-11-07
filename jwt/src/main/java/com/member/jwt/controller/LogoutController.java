package com.member.jwt.controller;

import com.member.jwt.jwt.JWTUtil;
import com.member.jwt.repository.RefreshRepository;
import com.member.jwt.service.TokenBlacklistService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth-logout")
public class LogoutController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public LogoutController(JWTUtil jwtUtil, RefreshRepository refreshRepository, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken, HttpServletRequest request, HttpServletResponse response) {

        // Bearer prefix 제거
        if (accessToken != null && accessToken.startsWith("Bearer ")) {

            accessToken = accessToken.substring(7);
        } else {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 엑세스 토큰입니다.");
        }

        // 엑세스 토큰을 블랙리스트에 추가
        Date expirationDate = jwtUtil.getExpiration(accessToken);
        long timeToExpire = expirationDate.getTime() - System.currentTimeMillis();
        tokenBlacklistService.addToBlacklist(accessToken, timeToExpire);

        // 쿠키에서 refresh 토큰 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || !jwtUtil.getCategory(refreshToken).equals("refresh")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 리프레시 토큰입니다.");
        }

        // refresh 토큰이 데이터베이스에 있는지 확인
        if (!refreshRepository.existsByRefresh(refreshToken)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 리프레시 토큰입니다.");
        }

        // DB에서 refresh 토큰 삭제
        refreshRepository.deleteByEmail(jwtUtil.getEmail(refreshToken));

        // 쿠키 만료시켜서 삭제
        Cookie deleteCookie = new Cookie("refresh", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);

        return ResponseEntity.ok("로그아웃 성공");
    }
}
