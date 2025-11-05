import { AuctionAnalysisDto } from '../../types/dto/response/auction';
import axiosInstance from '../app/utils/axiosInstance';

export const fetchAnalysis = async (id: number): Promise<AuctionAnalysisDto | null> => {
    if (!id) {
        return null;
    }

    try {
        const res = await axiosInstance.get(`/analysis/${id}/graphs`);
        return res.data;
    } catch (err: any) {
        console.error('fetchAnalysis => ' + err);
        return null;
    }
};

export const fetchAnalyzes = async (ids: Array<number>): Promise<Array<AuctionAnalysisDto>> => {
    if (!ids || ids.length === 0) {
        return [];
    }

    const idQuery = ids.join(',');

    try {
        const res = await axiosInstance.get(`/analysis/graphs`, {
            params: {
                ids: idQuery,
            },
        });
        return res.data;
    } catch (err: any) {
        console.error('fetchAnalyzes => ' + err);
        return [];
    }
};