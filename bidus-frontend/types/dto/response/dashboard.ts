export interface MonthlySummaryDto {
    sellerCount: number;
    winCount: number;
}

export interface CurrentBiddingCount {
    count: number;
}

export interface CategoryStatsDto {
    categoryName: string;
    count: number;
}

export interface DashboardStatsDto {
    monthlySummary: MonthlySummaryDto;
    biddingCount: CurrentBiddingCount;
    categories: Array<CategoryStatsDto>
}