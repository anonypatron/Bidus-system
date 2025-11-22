import { useQuery } from "@tanstack/react-query";
import { fetchDashboardStats } from '../api/dashboard';
import { useContext } from "react";
import { UserContext } from '../app/context/UserProvider';

export const useDashboardQuery = () => {
    const userContext = useContext(UserContext);
    
    return useQuery({
        queryKey: [userContext?.userInfo?.email],
        queryFn: fetchDashboardStats,
        staleTime: 1000 * 60 * 5,
        enabled: !!userContext,
    });
};
