package com.apiGateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/signup",
            "/api/auth/login",
            "/api/analysis",
            "/api/search"
    );
    private final JwtUtil jwtUtil; // JWT 검증 로직을 담은 클래스

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            HttpMethod method = request.getMethod();

            boolean isPublic = PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
            if (isPublic/* || isPublicPath(path, method) */) {
                return chain.filter(exchange);
            }

            if (isOptionalAuthPath(path, method)) {
                if (request.getCookies().containsKey("accessToken")) {
                    String token = Objects.requireNonNull(request.getCookies().getFirst("accessToken")).getValue();
                    if (jwtUtil.validateToken(token)) {
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", String.valueOf(userId))
                                .build();
                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    }
                }
                return chain.filter(exchange);
            }
            // 1. "accessToken" 쿠키 존재 여부 확인
            if (!request.getCookies().containsKey("accessToken")) {
                return handleUnauthorized(exchange); // 401 에러
            }

            // 2. 쿠키에서 토큰 추출 및 유효성 검증
            String token = Objects.requireNonNull(request.getCookies().getFirst("accessToken")).getValue();
            if (!jwtUtil.validateToken(token)) {
                return handleUnauthorized(exchange); // 401 에러
            }

            // 3. 토큰에서 사용자 ID 추출
            Long userId = jwtUtil.getUserIdFromToken(token);

            // 4. 요청 헤더에 사용자 ID 추가
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-ID", String.valueOf(userId))
                    .build();

//            log.info("게이트웨이 인증 통과: 사용자 ID [{}] 헤더 추가, 전달경로: {}", userId, path);
            // 5. 다음 필터 또는 서비스로 요청 전달
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }

    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

//    private boolean isPublicPath(String path, HttpMethod method) {
//        return path.equals("/api/auctions") && method == HttpMethod.GET;
//    }

    private boolean isOptionalAuthPath(String path, HttpMethod method) {
        if (path.matches("^/api/auctions/\\d+$") && method == HttpMethod.GET) {
            return true;
        }
        if (path.equals("/api/auctions") && method == HttpMethod.GET) {
            return true;
        }
        return false;
    }

    public static class Config {

    }

}