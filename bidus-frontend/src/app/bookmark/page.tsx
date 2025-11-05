'use client';

import { useEffect, useState } from 'react';
import { Auction, PageInfo } from '../../../types/dto/response/auction';
import { formatDateTime } from '../utils/formatDataTime';
import { useRouter } from 'next/navigation';
import axiosInstance from '../utils/axiosInstance';
import LoadingSpinner from '../components/LoadingSpinner';

function BookmarkPage() {
    const router = useRouter();
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [auctions, setAuctions] = useState<Array<Auction>>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [pageInfo, setPageInfo] = useState<PageInfo>({
        page: 0,
        totalPages: 0,
        totalElements: 0,
    });

    useEffect(() => {
        const fetchBookmarkedAuctions = async () => {
            setIsLoading(true);
            try {
                const res = await axiosInstance.get(`/auctions/bookmark?status=IN_PROGRESS&page=${currentPage}&size=10`);
                // console.log(res.data);
                setAuctions(res.data.content);
                setPageInfo({
                    page: res.data.number,
                    totalPages: res.data.totalPage,
                    totalElements: res.data.totalElement,
                });
            } catch (err: any) {
                console.error(err);
                setAuctions([]);
            } finally {
                setIsLoading(false);
            }
        };

        fetchBookmarkedAuctions();
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

    const handleGoToBidPage = (id: number) => {
        router.push(`/auctions?id=${id}`);
    };

    if (isLoading) {
        return <div>로딩 중...</div>
    }

    if (!auctions) {
        return <LoadingSpinner text="데이터를 불러오는 중..."/>
    }

    return (
        <div className="main-container">
            <h2>북마크한 경매 목록</h2>
            <hr/>
            <br/>
            <div className="auction-list">
                {auctions.map(auction => (
                    <div key={auction.id} className="auction-card" onClick={() => handleGoToBidPage(auction.id)}>
                        <img src={'http://localhost' + auction.imagePath} alt={auction.title} className="auction-card-image" />
                        <div className="auction-card-content">
                            <div className="auction-card-header">
                                <h3>{auction.title}</h3>
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

export default BookmarkPage;