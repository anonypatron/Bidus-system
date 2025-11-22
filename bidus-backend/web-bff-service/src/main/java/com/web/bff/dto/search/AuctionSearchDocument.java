package com.web.bff.dto.search;

import com.common.AuctionStatus;

import java.time.Instant;
import java.util.List;

public record AuctionSearchDocument(
        Long id,
        String title,
        String description,
        String imagePath,
        String sellerUserName,
        AuctionStatus status,
        Long startPrice,
        Long currentPrice,
        Instant startTime,
        Instant endTime,
        List<String> categories
) {
}
