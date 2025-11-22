import axiosInstance from '../app/utils/axiosInstance';
import { DashboardStatsDto } from '../../types/dto/response/dashboard';

export const fetchDashboardStats = async (): Promise<DashboardStatsDto> => {
    try {
        const res = await axiosInstance.get('/dashboard/stats');
        return res.data;
    } catch (err: any) {
        throw new Error('Failed to fetch dashboardStats');
    }
};
