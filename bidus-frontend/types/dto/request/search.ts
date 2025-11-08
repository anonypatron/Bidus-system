import { Auction, PageInfo } from '../response/auction';

export interface Sort {
    property: string;
    direction: 'asc' | 'desc';
}

export interface SearchParams {
    keyword?: string;
    status?: string;
    categories?: Array<string>;
    page?: number;
    size?: number;
    sort?: Sort;
}

export interface AuctionPage {
    content: Array<Auction>
    totalPages: number;
    totalElements: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
    empty: boolean;
    numberOfElements: number;
}