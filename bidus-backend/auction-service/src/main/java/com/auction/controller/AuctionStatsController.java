package com.auction.controller;

import com.auction.dto.response.CategoryStatsDto;
import com.auction.dto.response.CurrentBiddingCount;
import com.auction.dto.response.MonthlySummaryDto;
import com.auction.service.AuctionStatsService;
import com.common.dto.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auctions/stats")
public class AuctionStatsController {

    private final AuctionStatsService auctionStatsService;

    // 이번달 판매 횟수, 낙찰 횟수
    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        return ResponseEntity.status(HttpStatus.OK).body(auctionStatsService.getMonthlySummary(userId));
    }

    // 현재 입찰중인 경매 개수
    @GetMapping("/bidding-count")
    public ResponseEntity<CurrentBiddingCount> getBiddingCount(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        return ResponseEntity.status(HttpStatus.OK).body(auctionStatsService.getBiddingCount(userId));
    }

    @GetMapping("/top-categories")
    public ResponseEntity<List<CategoryStatsDto>> getTop10Categories(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        return ResponseEntity.status(HttpStatus.OK).body(auctionStatsService.getTop10Categories(userId)) ;
    }

}
