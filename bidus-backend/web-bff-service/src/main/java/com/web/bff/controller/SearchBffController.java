package com.web.bff.controller;

import com.web.bff.dto.search.AuctionSearchDocument;
import com.web.bff.service.SearchBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchBffController {

    private final SearchBffService searchBffService;

    @GetMapping("/auctions")
    public Mono<ResponseEntity<Page<AuctionSearchDocument>>> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return searchBffService.search(keyword, status, categories, PageRequest.of(page, size))
                .map(ResponseEntity::ok);
    }

}
