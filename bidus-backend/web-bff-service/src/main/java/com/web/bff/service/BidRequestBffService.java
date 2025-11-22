package com.web.bff.service;

import com.web.bff.dto.bidrequest.BidRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Slf4j
public class BidRequestBffService {

    private final WebClient webClient;

    public Mono<Void> placeBid(Long userId, BidRequestDto dto) {
        return webClient
                .post()
                .uri("http://bid-request-service/api/bids")
                .header("X-User-ID", String.valueOf(userId))
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(Void.class);
    }

}
