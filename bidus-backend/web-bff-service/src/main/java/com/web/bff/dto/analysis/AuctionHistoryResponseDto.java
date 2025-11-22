package com.web.bff.dto.analysis;

import com.common.AuctionStatus;

import java.time.Instant;
import java.util.List;

public record AuctionHistoryResponseDto(
        String title,

        Long startPrice,
        Long finalPrice,
        Long totalBidCount,

        AuctionStatus status,

        Instant startTime,
        Instant endTime,

        List<GraphPointDto>bidHistoryGraph
) {
}
