package com.web.bff.controller;

import com.common.dto.user.UserPrincipal;
import com.web.bff.dto.auth.LoginRequestDto;
import com.web.bff.dto.auth.SignUpRequestDto;
import com.web.bff.dto.auth.UpdateUserRequestInfoDto;
import com.web.bff.dto.auth.UserInfo;
import com.web.bff.service.AuthBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/")
public class AuthBffController {

    private final AuthBffService authBffService;

    @GetMapping("/users/info")
    public Mono<ResponseEntity<UserInfo>> getUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return authBffService.getUserInfo(userPrincipal.getId())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<Void>> signup(
            @RequestBody SignUpRequestDto signUpRequestDto
    ) {
        return authBffService.save(signUpRequestDto)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> login(
            @RequestBody LoginRequestDto requestDto,
            ServerWebExchange exchange
    ) {
        return authBffService.login(requestDto)
                .doOnNext(responseEntity -> {
                    List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
                    if (cookies != null) {
                        cookies.forEach(cookie ->
                                exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, cookie)
                        );
                    }
                })
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            ServerWebExchange exchange
    ) {
        return authBffService.logout(refreshToken)
                .doOnNext(responseEntity -> {
                    List<String> cookies = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
                    if (cookies != null) {
                        cookies.forEach(cookie ->
                                exchange.getResponse().getHeaders().add(HttpHeaders.SET_COOKIE, cookie)
                        );
                    }
                })
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PatchMapping
    public Mono<ResponseEntity<Void>> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateUserRequestInfoDto dto
    ) {
        return authBffService.updateUser(principal.getId(), dto)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteUser(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return authBffService.deleteUser(principal.getId())
                .then(Mono.just(ResponseEntity.ok().build()));
    }

}
