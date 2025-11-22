package com.web.bff.service;

import com.web.bff.dto.search.AuctionSearchDocument;
import com.web.bff.helper.RestPageImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SearchBffService {

    private final WebClient webClient;

    public Mono<Page<AuctionSearchDocument>> search(
            String keyword, String status, List<String> categories, Pageable pageable
    ) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString("http://search-service/api/search/auctions")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        pageable.getSort().stream().forEach(order -> {
            uriBuilder.queryParam("sort", order.getProperty() + "," + order.getDirection());
        });

        if (keyword != null && !keyword.isEmpty()) {
            uriBuilder.queryParam("keyword", keyword);
        }
        if (status != null && !status.isEmpty()) {
            uriBuilder.queryParam("status", status);
        }
        if (categories != null && !categories.isEmpty()) {
            uriBuilder.queryParam("categories", categories.toArray());
        }

        return webClient
                .get()
                .uri(uriBuilder.toUriString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<RestPageImpl<AuctionSearchDocument>>() {})
                .map(page -> page);
    }

}
