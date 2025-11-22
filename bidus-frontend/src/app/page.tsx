'use client';

import { useRouter } from "next/navigation";
import { useContext, useEffect, useState } from "react";
import { formatDateTime } from './utils/formatDataTime';
import { Auction, PageInfo } from "../../types/dto/response/auction";
import axiosInstance from './utils/axiosInstance';
import { UserContext } from "./context/UserProvider";
import { LoadingSpinner } from "./components/others/LoadingSpinner";
import EmptyComponent from "./components/others/EmptyComponent";

function Home() {
    const router = useRouter();
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [auctions, setAuctions] = useState<Array<Auction>>([]);
    const [pageInfo, setPageInfo] = useState<PageInfo>({
        page: 0,
        totalPages: 0,
        totalElements: 0,
    });
    
    const fetchAllInprogressAuctions = async () => {
        try {
            const res = await axiosInstance.get(`/auctions?status=IN_PROGRESS&page=${currentPage}&size=9`);
            
            setAuctions(res.data.content);
            setPageInfo({
                page: res.data.number,
                totalPages: res.data.totalPages,
                totalElements: res.data.totalElements,
            });
        } catch (err: any) {
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchAllInprogressAuctions();
    }, [currentPage]);

    const renderPageNumbers = () => {
        const pages = [];
        const maxPagesToShow = 5;
        const half = Math.floor(maxPagesToShow / 2);
        let startPage = Math.max(0, currentPage - half);
        let endPage = Math.min(pageInfo.totalPages - 1, startPage + maxPagesToShow - 1);

        if (endPage - startPage + 1 < maxPagesToShow) {
            startPage = Math.max(0, endPage - maxPagesToShow + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            pages.push(
                <button 
                    key={i} 
                    onClick={() => setCurrentPage(i)} 
                    disabled={currentPage === i}
                    className={`pagination-button ${currentPage === i ? 'active' : ''}`}
                >
                    {i + 1}
                </button>
            );
        }
        return pages;
    };

    const userContext = useContext(UserContext);
    const handleBookmarkToggle = async (e: React.MouseEvent<HTMLDivElement, MouseEvent>, auctionId: number) => {
        e.stopPropagation();

        if (!userContext?.userInfo) {
            if (confirm('로그인 후 이용가능합니다. 로그인 페이지로 이동하시겠습니까?')) {
                router.push('/login');
            }
            return;
        }

        const updateAuctions = auctions.map(auction =>
            auction.id === auctionId ? { ...auction, isBookmarked: !auction.bookmarked } : auction
        );
        setAuctions(updateAuctions);

        const targetAuction = updateAuctions.find(auction => auction.id === auctionId);
        if (!targetAuction) {
            return;
        }

        try {
            if (targetAuction.bookmarked) {
                const res = await axiosInstance.delete(`/bookmarks/${auctionId}`);
            }
            else {
                const res = await axiosInstance.post(`/bookmarks/${auctionId}`);
            }
            fetchAllInprogressAuctions();
        } catch (err: any) {
            console.error(err);
        }
    };

    const handleGoToBidPage = (id: number) => {
        router.push(`/auctions?id=${id}`);
    };

    if (isLoading) {
        return <LoadingSpinner/>
    }

    if (!auctions || auctions.length === 0) {
        return <EmptyComponent content="진행중인 경매가 없습니다."/>
    }

    return (
        <div className="main-container">
            <h2>진행중인 경매 목록</h2>
            <hr/>
            <br/>
            <div className="auction-list">
                {auctions.map(auction => (
                    <div key={auction.id} className="auction-card" onClick={() => handleGoToBidPage(auction.id)}>
                        <img src={'http://localhost' + auction.imagePath} alt={auction.title} className="auction-card-image" />
                        <div className="auction-card-content">
                            <div className="auction-card-header">
                                <h3>{auction.title}</h3>
                                <div 
                                    className="bookmark-container" 
                                    onClick={(e) => handleBookmarkToggle(e, auction.id)}
                                >
                                    <svg 
                                        className={`bookmark-icon ${auction.bookmarked ? 'active' : ''}`}
                                        xmlns="http://www.w3.org/2000/svg" 
                                        viewBox="0 0 24 24" 
                                        width="24" 
                                        height="24"
                                    >
                                        <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                                    </svg>
                                </div>
                            </div>
                            <p className="auction-card-description">{auction.description}</p>
                            <div className="auction-tags">
                                {auction.categories.map((tag, index) => (
                                    <span key={index} className="auction-tag">{tag}</span>
                                ))}
                            </div>
                            <div className="auction-card-info">
                                <p><strong>판매자:</strong> {auction.sellerUserName}</p>
                                <p><strong>시작가:</strong> {auction.startPrice}원</p>
                                <p><strong>현재가:</strong> {auction.currentPrice}원</p>
                                <p><strong>종료 시간:</strong> {formatDateTime(auction.endTime)}</p>
                            </div>
                        </div>
                    </div>
                ))}
            </div>

            <div className="pagination-container">
                <button 
                    onClick={() => setCurrentPage(currentPage - 1)} 
                    disabled={currentPage === 0}
                    className="pagination-button"
                >
                    이전
                </button>

                {renderPageNumbers()}

                <button 
                    onClick={() => setCurrentPage(currentPage + 1)} 
                    disabled={currentPage === pageInfo.totalPages - 1}
                    className="pagination-button"
                >
                    다음
                </button>
            </div>
        </div>
    );
}

export default Home;