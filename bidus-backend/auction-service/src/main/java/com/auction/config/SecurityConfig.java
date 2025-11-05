package com.auction.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final String[] PERMIT_ALL_URLS = {
            "/api/auctions",
            "/api/auctions/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserIdAuthenticationFilter userIdAuthenticationFilter
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, PERMIT_ALL_URLS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/history").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auctions").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/auctions").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(userIdAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
