package com.web.bff.config;

import com.common.dto.user.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserIdAuthenticationFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userIdHeader = exchange.getRequest().getHeaders().getFirst("X-User-ID");

        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            Long userId = Long.parseLong(userIdHeader);
            UserPrincipal principal = new UserPrincipal(userId);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            // [4. Reactive SecurityContext에 저장]
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        }

        // 헤더가 없으면 "손님"으로 컨텍스트를 비우고 체인 계속
        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.clearContext());
    }

}
