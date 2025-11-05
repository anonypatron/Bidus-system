package com.generator.dto;

import java.time.Instant;
import java.util.List;

public record AuctionCreateRequestDto(
        String title,
        String description,
        List<String> categories,
        Long startPrice,
        Instant startTime,
        Instant endTime
) {}
