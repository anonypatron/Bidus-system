export enum AuctionStatus {
    SCHEDULED, IN_PROGRESS, CLOSED, CANCELLED
}

export interface Auction {
    id: number;
    title: string;
    description: string;
    imagePath: string;
    // sellerId: number;
    sellerUserName: string;
    status: AuctionStatus;
    startPrice: number;
    currentPrice: number;
    startTime: string;
    endTime: string;
    categories: Array<string>;
    // winnerId?: number | null;
    finalPrice?: number | null;
    isBookmarked: boolean;
}

export interface PageInfo {
    page: number;
    totalPages: number;
    totalElements: number;
}

export interface AuctionPage {
    auctions: Array<Auction>;
    pageInfo: PageInfo
}

export interface AuctionAnalysisDto {
    title: string;
    startPrice: number;
    finalPrice: number;
    totalBidCount: number;
    status: AuctionStatus;
    startTime: string;
    endTime: string; // ISO형식
    bidHistoryGraph: Array<GraphPoint>;
}

export interface GraphPoint {
    time: string; // 입찰 시간
    price: number;
    userId: number;
}
