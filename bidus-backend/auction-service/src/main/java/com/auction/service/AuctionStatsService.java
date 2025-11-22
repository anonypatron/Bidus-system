package com.auction.service;

import com.auction.dto.response.CategoryStatsDto;
import com.auction.dto.response.CurrentBiddingCount;
import com.auction.dto.response.MonthlySummaryDto;
import com.auction.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuctionStatsService {

    private final AuctionRepository auctionRepository;

    public MonthlySummaryDto getMonthlySummary(Long userId) {
        Long sellCount = auctionRepository.findSellCountThisMonth(userId);
        Long winCount = auctionRepository.findWinCountThisMonth(userId);

        return MonthlySummaryDto.builder()
                .sellCount(sellCount)
                .winCount(winCount)
                .build();
    }

    public CurrentBiddingCount getBiddingCount(Long userId) {
        return CurrentBiddingCount.builder()
                .count(auctionRepository.findCurrentBiddingCount(userId))
                .build();
    }

    public List<CategoryStatsDto> getTop10Categories(Long userId) {
        return auctionRepository.findTop10Categories(userId);
    }

}
