package com.web.bff.dto.analysis;

import java.time.Instant;

public record GraphPointDto(
        Instant time,
        Long price,
        Long userId
) {
}
