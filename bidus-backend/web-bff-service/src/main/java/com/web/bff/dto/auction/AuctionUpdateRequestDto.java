package com.web.bff.dto.auction;

import java.time.Instant;
import java.util.List;

public record AuctionUpdateRequestDto(
        String title,
        String description,
        List<String>categories,
        Long startPrice,

        Instant startTime,
        Instant endTime
) {
}
