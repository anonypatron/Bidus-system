package com.web.bff.controller;

import com.web.bff.dto.analysis.AuctionHistoryResponseDto;
import com.web.bff.service.AnalysisBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis")
public class AnalysisBffController {

    private final AnalysisBffService analysisBffService;

    @GetMapping("/{auctionId}/graphs")
    public Mono<ResponseEntity<AuctionHistoryResponseDto>> getAnalysis(
            @PathVariable("auctionId") Long auctionId
    ) {
        return analysisBffService.getAuctionAnalysis(auctionId)
                .map(ResponseEntity::ok);
    }

    // 2. 다중 경매 분석
    @GetMapping("/graphs")
    public Mono<ResponseEntity<List<AuctionHistoryResponseDto>>> getAnalyzes(
            @RequestParam("ids") List<Long> auctionIds
    ) {
        return analysisBffService.getAuctionAnalyzes(auctionIds)
                .map(ResponseEntity::ok);
    }

}
