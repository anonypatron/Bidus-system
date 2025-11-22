export interface UserInfo {
    username: string;
    email: string;
    role: 'USER' | 'ADMIN';
}

// useContext용 사용자 정보
export interface UserContextType {
    userInfo: UserInfo | null;
    refreshUserInfo: () => Promise<void>; // 사용자 정보 리프레시
    setUserInfo: React.Dispatch<React.SetStateAction<UserInfo | null>>; // nav bar에 정보 표시
    logout: () => Promise<boolean>; // 로그아웃
}