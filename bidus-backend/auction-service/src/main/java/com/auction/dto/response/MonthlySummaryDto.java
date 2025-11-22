package com.auction.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MonthlySummaryDto {

    private Long sellCount;
    private Long winCount;

    @Builder
    public MonthlySummaryDto(Long sellCount, Long winCount) {
        this.sellCount = sellCount;
        this.winCount = winCount;
    }

}
