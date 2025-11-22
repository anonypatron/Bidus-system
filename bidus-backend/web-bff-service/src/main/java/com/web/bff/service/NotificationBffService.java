package com.web.bff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationBffService {

    private final WebClient webClient;

    public Flux<String> subscribe(Long userId, Long auctionId) {
        return webClient
                .get()
                .uri("http://notification-service/api/notifications/{auctionId}/subscribe", auctionId)
                .header("X-User-ID", String.valueOf(userId))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(String.class);
    }

}
