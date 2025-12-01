import { AuctionPage } from "../../types/dto/request/search"
import { SearchParams } from '../../types/dto/request/search';
import axiosInstance from '../app/utils/axiosInstance';

const BASE_URL = process.env.NEXT_PUBLIC_BASE_URL;

export const fetchSearchByKeyword = async (params: SearchParams): Promise<AuctionPage> => {
    const query = new URLSearchParams();

    if (params.keyword) {
        query.append('keyword', params.keyword);
    }
    if (params.status) {
        query.append('status', params.status);
    }
    if (params.categories) {
        params.categories.map(category => query.append('categories', category));
    }

    if (params.page !== undefined) {
        query.append('page', String(params.page));
    }
    if (params.size !== undefined) {
        query.append('size', String(params.size));
    }

    if (params.sort) {
        query.append('sort', `${params.sort.property},${params.sort.direction}`); // spring pageable
    }
    
    const queryString = query.toString();
    const API_URL = `${BASE_URL}/search/auctions?${queryString}`;

    try {
        const res = await axiosInstance.get(API_URL);
        return res.data;
    } catch (err: any) {
        console.error('fetchSearchByKeyword error => ' + err);
        throw new Error('Failed to fetch auctions');
    }
};