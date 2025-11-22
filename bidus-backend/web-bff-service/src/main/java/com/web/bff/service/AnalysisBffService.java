package com.web.bff.service;

import com.web.bff.dto.analysis.AuctionHistoryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnalysisBffService {

    private final WebClient webClient;

    public Mono<AuctionHistoryResponseDto> getAuctionAnalysis(Long auctionId) {
        return webClient
                .get()
                .uri("http://analysis-service/api/analysis/{auctionId}/graphs", auctionId)
                .retrieve()
                .bodyToMono(AuctionHistoryResponseDto.class);
    }

    public Mono<List<AuctionHistoryResponseDto>> getAuctionAnalyzes(List<Long> auctionIds) {
        String uriString = UriComponentsBuilder
                .fromUriString("http://analysis-service/api/analysis/graphs")
                .queryParam("ids", auctionIds)
                .toUriString();

        return webClient
                .get()
                .uri(uriString)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

}
