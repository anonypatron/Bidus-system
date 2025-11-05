package com.analysis.controller;

import com.analysis.dto.AuctionHistoryResponseDto;
import com.analysis.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    // 상태가 종료된 경매가 검색 가능
    @GetMapping("/{auctionId}/graphs")
    public ResponseEntity<AuctionHistoryResponseDto> getAnalysis(
            @PathVariable("auctionId") Long auctionId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(analysisService.getAuctionAnalysis(auctionId));
    }

    @GetMapping("/graphs")
    public ResponseEntity<List<AuctionHistoryResponseDto>> getAnalyzes(
            @RequestParam("ids") List<Long> auctionIds
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(analysisService.getAuctionAnalyzes(auctionIds));
    }

}
