package com.web.bff.service;

import com.web.bff.dto.stats.CategoryStatsDto;
import com.web.bff.dto.stats.DashboardResponseDto;
import com.web.bff.dto.stats.MonthlySummaryDto;
import com.web.bff.helper.CurrentBiddingCount;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DashboardService {

    private final WebClient webClient;

    public Mono<DashboardResponseDto> getDashboardStats(Long userId) {
        Mono<MonthlySummaryDto> summaryMono = webClient.get()
                .uri("http://auction-service/api/auctions/stats/monthly-summary")
                .header("X-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(MonthlySummaryDto.class);

        Mono<CurrentBiddingCount> biddingCountMono = webClient.get()
                .uri("http://auction-service/api/auctions/stats/bidding-count")
                .header("X-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(CurrentBiddingCount.class);

        Mono<List<CategoryStatsDto>> categoriesMono = webClient.get()
                .uri("http://auction-service/api/auctions/stats/top-categories")
                .header("X-User-Id", String.valueOf(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});

        return Mono.zip(summaryMono, biddingCountMono, categoriesMono)
                .map(tuple -> DashboardResponseDto.builder()
                        .monthlySummary(tuple.getT1())
                        .biddingCount(tuple.getT2())
                        .categories(tuple.getT3())
                        .build());
    }

}
