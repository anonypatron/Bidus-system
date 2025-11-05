package com.analysis.dto;

import com.analysis.entity.AuctionHistory;
import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class AuctionHistoryResponseDto {

    private String title;

    private Long startPrice;
    private Long finalPrice;
    private Long totalBidCount;

    private AuctionStatus status;

    private Instant startTime;
    private Instant endTime;

    private List<GraphPointDto> bidHistoryGraph;

    @Builder
    public AuctionHistoryResponseDto(
            String title,
            Long startPrice,
            Long finalPrice,
            Long totalBidCount,
            AuctionStatus status,
            Instant startTime,
            Instant endTime,
            List<GraphPointDto> bidHistoryGraph
    ) {
        this.title = title;
        this.startPrice = startPrice;
        this.finalPrice = finalPrice;
        this.totalBidCount = totalBidCount;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bidHistoryGraph = bidHistoryGraph;
    }

    public static AuctionHistoryResponseDto fromEntity(AuctionHistory auctionHistory) {
        return AuctionHistoryResponseDto.builder()
                .title(auctionHistory.getTitle())
                .startPrice(auctionHistory.getStartPrice())
                .finalPrice(auctionHistory.getFinalPrice())
                .totalBidCount((long) auctionHistory.getBidHistoryGraph().size())
                .status(auctionHistory.getStatus())
                .startTime(auctionHistory.getStartTime())
                .endTime(auctionHistory.getEndTime())
                .bidHistoryGraph(auctionHistory.getBidHistoryGraph())
                .build();
    }

}
