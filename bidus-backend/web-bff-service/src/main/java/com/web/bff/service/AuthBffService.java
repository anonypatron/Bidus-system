package com.web.bff.service;

import com.web.bff.dto.auth.LoginRequestDto;
import com.web.bff.dto.auth.SignUpRequestDto;
import com.web.bff.dto.auth.UpdateUserRequestInfoDto;
import com.web.bff.dto.auth.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthBffService {

    private final WebClient webClient;

    public Mono<UserInfo> getUserInfo(Long userId) {
        return webClient
                .get()
                .uri("http://auth-service/api/auth/users/info")
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(UserInfo.class);
    }

    public Mono<Void> save(SignUpRequestDto signUpRequestDto) {
        return webClient
                .post()
                .uri("http://auth-service/api/auth/signup")
                .bodyValue(signUpRequestDto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<ResponseEntity<Void>> login(LoginRequestDto requestDto) {
        return webClient
                .post()
                .uri("http://auth-service/api/auth/login")
                .bodyValue(requestDto)
                .retrieve()
                .toBodilessEntity();
    }

    public Mono<ResponseEntity<Void>> logout(String refreshToken) {
        return webClient
                .post()
                .uri("http://auth-service/api/auth/logout")
                .cookie("refreshToken", refreshToken)
                .retrieve()
                .toBodilessEntity();
    }

    public Mono<Void> updateUser(Long userId, UpdateUserRequestInfoDto dto) {
        return webClient
                .patch()
                .uri("http://auth-service/api/auth/")
                .header("X-User-ID", String.valueOf(userId))
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> deleteUser(Long userId) {
        return webClient
                .delete()
                .uri("http://auth-service/api/auth/")
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
