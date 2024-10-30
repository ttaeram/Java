package com.blog.blog.service;

import com.blog.blog.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {

        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long UserId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(UserId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
