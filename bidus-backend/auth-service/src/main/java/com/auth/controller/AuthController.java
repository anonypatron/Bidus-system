package com.auth.controller;

import com.auth.config.jwt.JwtProperties;
import com.auth.config.jwt.TokenProvider;
import com.auth.dto.request.LoginRequestDto;
import com.auth.dto.request.SignUpRequestDto;
import com.auth.dto.request.UpdateUserRequestInfoDto;
import com.auth.dto.response.UserInfo;
import com.auth.entity.User;
import com.auth.service.TokenService;
import com.auth.service.UserService;
import com.common.dto.user.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenService tokenService;

    @GetMapping("/users/info")
    public ResponseEntity<UserInfo> getUserInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(userId));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequestDto signUpRequestDto) {
        if (userService.save(signUpRequestDto)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response
    ) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            tokenService.saveOrUpdateRefreshToken(user.getId(), refreshToken, Duration.ofDays(7));
            // HttpOnly 쿠키에 access Token 저장
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(jwtProperties.getAccessTokenExpirationMs())
                    .secure(false)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

            // HttpOnly 쿠키에 Refresh Token 저장 (ResponseCookie 사용)
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(jwtProperties.getRefreshTokenExpirationMs())
                    .secure(false)
                    .sameSite("Lax")
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.ok().build();
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response,
            @CookieValue(name = "refreshToken", required = false)
            String refreshToken
    ) {
        if (refreshToken != null) {
            // Refresh Token을 DB에서 삭제하여 무효화
            try {
                // JWT에서 userId 추출 (RefreshToken 유효성 검사 필요)
                Long userId = tokenProvider.getUserIdFromToken(refreshToken);
                tokenService.deleteRefreshToken(userId);
            } catch (Exception e) {
                // 토큰이 유효하지 않거나 파싱할 수 없는 경우 처리
                System.err.println("Error processing refresh token during logout: " + e.getMessage());
            }
        }

        // 클라이언트 쿠키 무효화
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true).path("/").maxAge(0).secure(false).sameSite("Lax").build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true).path("/").maxAge(0).secure(false).sameSite("Lax").build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateUserRequestInfoDto dto
    ) {
        Long userId = principal.getId();
        userService.updateUser(userId, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long userId = principal.getId();
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
