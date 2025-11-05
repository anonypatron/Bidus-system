package com.auth.controller;

import com.auth.config.jwt.JwtProperties;
import com.auth.config.jwt.TokenProvider;
import com.auth.entity.RefreshToken;
import com.auth.entity.User;
import com.auth.service.TokenService;
import com.auth.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/token")
public class TokenController {

    private final JwtProperties jwtProperties;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found in cookie.");
        }

        // 1. JWT 토큰 유효성 검사 (서명, 만료 여부)
        if (!tokenProvider.validToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token.");
        }

        // 2. DB에 저장된 Refresh Token과 일치하는지 확인
        RefreshToken storedRefreshToken = tokenService.findByRefreshToken(refreshToken);
        if (storedRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found in DB.");
        }

        // 3. Refresh Token의 만료 시간 확인 (DB 기준)
        if (storedRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            // DB의 Refresh Token도 만료되었다면 삭제하고 UNATHORIZED
            tokenService.deleteRefreshToken(storedRefreshToken.getUserId()); // 만료된 토큰 삭제
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired refresh token in DB. Please re-login.");
        }

        // 4. 새로운 Access Token 및 Refresh Token 발급 (Refresh Token Rotation)
        User user = userService.findById(storedRefreshToken.getUserId());
        String newAccessToken = tokenProvider.generateAccessToken(user);
        String newRefreshToken = tokenProvider.generateRefreshToken(user);

        // 5. DB의 Refresh Token 업데이트 (기존 토큰 무효화)
        tokenService.updateRefreshToken(user.getId(), newRefreshToken, Duration.ofDays(7));

        // 6. 새로운 토큰들을 HTTP Only 쿠키로 전송
        ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpirationMs())
                .secure(false)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, newAccessTokenCookie.toString());

        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenExpirationMs())
                .secure(false)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, newRefreshTokenCookie.toString());

        return ResponseEntity.ok().build(); // 응답 바디 없이 성공 반환
    }

}
