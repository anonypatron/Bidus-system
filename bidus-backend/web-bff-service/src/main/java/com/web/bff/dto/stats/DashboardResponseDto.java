package com.web.bff.dto.stats;

import com.web.bff.helper.CurrentBiddingCount;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DashboardResponseDto {

    private MonthlySummaryDto monthlySummary;
    private CurrentBiddingCount biddingCount;
    private List<CategoryStatsDto> categories;

    @Builder
    public DashboardResponseDto(
            MonthlySummaryDto monthlySummary,
            CurrentBiddingCount biddingCount,
            List<CategoryStatsDto> categories
    ) {
        this.monthlySummary = monthlySummary;
        this.biddingCount = biddingCount;
        this.categories = categories;
    }

}
