package com.web.bff.dto.stats;

public record CategoryStatsDto(
        String categoryName,
        Long count
) {
}
