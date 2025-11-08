'use client';

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useContext, useEffect } from "react";
import { RiAuctionFill } from "react-icons/ri";
import { NavbarProps } from '../../../types/others/navbar';
import { UserContext } from "../context/UserProvider";
import SearchBox from './SearchBox';

function Navbar({ onToggleSidebar }: NavbarProps) {
    
    const router = useRouter();
    const userContext = useContext(UserContext);

    if (!userContext) {
        console.log("Navbar userContext error");
        return null;
    }

    const { userInfo, refreshUserInfo, setUserInfo, logout } = userContext;

    useEffect(() => {
        refreshUserInfo();
    }, []);

    const handleLogout = async () => {
        try {
            await logout();
            router.refresh();
        } catch (error) {
            console.error('로그아웃 실패:', error);
            alert('로그아웃 실패.');
        }
    };

    if (userInfo) {
        return (
            <nav className="navbar">
                <button onClick={onToggleSidebar} className="navbar-toggle-btn">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
                    </svg>
                </button>
                <div className="navbar-left">
                    <RiAuctionFill/>
                    <Link href="/" className="navbar-link navbar-brand">
                        Bidus
                    </Link>
                    {(userInfo.role === 'ADMIN') && (
                    <Link href="/admin" className="navbar-link">
                        ADMIN
                    </Link>
                    )}
                </div>

                <div className="navbar-center">
                    <SearchBox />
                </div>

                <div className="navbar-right">
                    <span className="navbar-text">안녕하세요, {userInfo.username} 님!</span>
                    <button onClick={handleLogout} className="navbar-button logout-button">
                        로그아웃
                    </button>
                </div>
            </nav>
        );
    }
    else {
        return (
            <nav className="navbar">
                <button onClick={onToggleSidebar} className="navbar-toggle-btn">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
                    </svg>
                </button>
                <div className="navbar-left">
                    <RiAuctionFill/>
                    <Link href="/" className="navbar-link navbar-brand">
                        Online Auction
                    </Link>
                </div>

                <div className="navbar-center">
                    <SearchBox />
                </div>
                
                <div className="navbar-right">
                    {/* <span className="navbar-text">로그인이 필요합니다.</span> */}
                    <Link href="/signup" className="navbar-link">
                        회원가입
                    </Link>
                    <Link href="/login" className="navbar-button">
                        로그인
                    </Link>
                </div>
            </nav>
        );
    }
}

export default Navbar;