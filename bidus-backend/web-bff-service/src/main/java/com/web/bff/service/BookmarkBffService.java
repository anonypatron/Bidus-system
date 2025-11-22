package com.web.bff.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class BookmarkBffService {

    private final WebClient webClient;

    public Mono<List<Long>> getBookmarkedAuctionIdsByUserId(Long userId) {
        return webClient
                .get()
                .uri("http://bookmark-service/api/bookmarks")
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<Void> save(Long userId, Long auctionId) {
        return webClient
                .post()
                .uri("http://bookmark-service/api/bookmarks/{auctionId}", auctionId)
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> delete(Long userId, Long auctionId) {
        return webClient
                .delete()
                .uri("http://bookmark-service/api/bookmarks/{auctionId}", auctionId)
                .header("X-User-ID", String.valueOf(userId))
                .retrieve()
                .bodyToMono(Void.class);
    }

}
