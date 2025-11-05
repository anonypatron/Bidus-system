'use client';

import { createContext, useCallback, useEffect, useMemo, useState } from "react";
import { UserContextType, UserInfo } from "../../../types/dto/user/user";
import { getCookie } from "cookies-next";
import axiosInstance from "../utils/axiosInstance";

export const UserContext = createContext<UserContextType | undefined>(undefined);

function UserProvider({ children }: { children: React.ReactNode }) {
    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

    const fetchUserInfo = useCallback(async () => {
        try {
            const infoRes = await axiosInstance.get('/auth/users/info');
            setUserInfo(infoRes.data);
        } catch (error: any) {
            console.warn('Failed to fetch user info');
            setUserInfo(null);
        }
    }, []);

    useEffect(() => {
        const accessToken = getCookie('accessToken');
        if (accessToken) {
            fetchUserInfo();
        }
        else {
            setUserInfo(null);
        }
    }, [fetchUserInfo]);

    const refreshUserInfo = useCallback(async () => {
        await fetchUserInfo();
    }, [fetchUserInfo]);

    const logout = useCallback(async () => {
        try {
            await axiosInstance.post('/auth/logout');
            setUserInfo(null);
            refreshUserInfo();
            return true;
        } catch (error) {
            console.error('로그아웃 실패:', error);
            // 에러 처리 (예: 알림)
            return false;
        }
    }, []);

    const contextValue = useMemo(() => {
        return { 
            userInfo, 
            refreshUserInfo, 
            setUserInfo, 
            logout
        }
    }, [userInfo, refreshUserInfo, setUserInfo, logout]);

    return (
        <UserContext.Provider value={contextValue}>
            {children}
        </UserContext.Provider>
    );
}

export default UserProvider;