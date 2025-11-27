package com.web.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private final String[] PERMIT_ALL_URLS = {
            "/actuator/**",
            "/api/analysis/**",
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/logout",
            "/api/search/auctions"
    };

    @Bean
    public SecurityWebFilterChain securityFilterChain(
            ServerHttpSecurity http,
            UserIdAuthenticationFilter userIdAuthenticationWebFilter
    ) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(PERMIT_ALL_URLS).permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/auctions").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/auctions/\\d+").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(userIdAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

} 
