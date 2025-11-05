import { AuctionPage } from '../../types/dto/response/auction';
import axiosInstance from '../app/utils/axiosInstance';
import { Auction } from '../../types/dto/response/auction';

export const fetchAllInprogressAuctions = async (currentPage: number): Promise<AuctionPage> => {
    const res = await axiosInstance.get(`/auctions?status=IN_PROGRESS&page=${currentPage}&size=9`);
    const data = res.data;

    return {
        auctions: data.content,
        pageInfo: {
            page: data.number,
            totalPages: data.totalPages,
            totalElements: data.totalElements,
        },
    };
};

export const fetchAuctionHistory = async (role: 'seller' | 'winner', status: 'CLOSED' | 'IN_PROGRESS' | 'SCHEDULED'): Promise<Array<Auction>> => {
    const res = await axiosInstance.get(`/auctions/history?role=${role}&status=${status}`);
    return res.data;
};

export const deleteAuction = async (id: number): Promise<void> => {
    await axiosInstance.delete(`/auctions/${id}`);
};