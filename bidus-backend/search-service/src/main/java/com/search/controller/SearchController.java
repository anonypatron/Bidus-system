package com.search.controller;

import com.search.entity.AuctionSearchDocument;
import com.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/auctions")
    public ResponseEntity<Page<AuctionSearchDocument>> searchAuctions(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<String> categories,
            @PageableDefault(page = 0, size = 9) Pageable pageable
    ) {
        Page<AuctionSearchDocument> result = searchService.search(
                keyword, status, categories, pageable
        );

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
