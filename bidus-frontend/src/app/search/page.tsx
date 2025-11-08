'use client';

import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";
import { PageInfo } from "../../../types/dto/response/auction";
import { useAuctionSearch } from '../../hooks/useAuctionsQuery';
import { toast } from '../../lib/toast';
import EmptyComponent from "../components/EmptyComponent";
import ErrorComponent from "../components/ErrorComponent";
import LoadingSpinner from "../components/LoadingSpinner";
import { formatDateTime } from "../utils/formatDataTime";

function SearchPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const pathname = usePathname();
    const [pageInfo, setPageInfo] = useState<PageInfo>({
        page: 0,
        totalPages: 0,
        totalElements: 0,
    });

    const keyword = searchParams.get('keyword') || undefined;
    const status = searchParams.get('status') || undefined;
    const categories = searchParams.getAll('categories') || undefined;
    const sortString = searchParams.get('sort');
    const currentPage = Number(searchParams.get('page') || 0);

    let sortProperty = 'endTime';
    let sortDirection: 'asc' | 'desc' = 'desc';

    if (sortString) {
        const parts = sortString.split(',');
        if (parts.length === 2) {
            sortProperty = parts[0];
            
            // type 범위를 좁혀줘야 함.
            const directionStr = parts[1].toLowerCase();

            if (directionStr === 'asc' || directionStr ==='desc') {
                sortDirection = directionStr;
            }
        }
    }

    const { data: auctions, isLoading, isError, error } = useAuctionSearch({
        keyword: keyword,
        status: status,
        categories: categories,
        page: currentPage,
        size: 9,
        sort: {
            property: sortProperty,
            direction: sortDirection,
        },
    });

    useEffect(() => {
        if (auctions) {
            setPageInfo({
                page: auctions.number,
                totalPages: auctions.totalPages,
                totalElements: auctions.totalElements,
            });
        }
    }, [auctions]);

    const handlePageChange = (newPage: number) => {
        const currentParams = new URLSearchParams(Array.from(searchParams.entries()));
        
        currentParams.set('page', String(newPage));
        
        router.push(`${pathname}?${currentParams.toString()}`);
    };
    
    const handleGoToBidPage = (id: number, status: string) => {
        if (status === 'CANCELLED') {
            toast.custom('취소된 경매입니다.')
            return;
        }
        else if (status === 'CLOSED') {
            toast.warning('종료된 경매입니다.');
            return;
        }
        else if (status === 'SCHEDULED') {
            toast.info('예정된 경매입니다.', {
                description: '시작 후 이용해주시길 바랍니다.',
            });
            return;
        }
        router.push(`/auctions?id=${id}`);
    };

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
                    onClick={() => handlePageChange(i)} 
                    disabled={currentPage === i}
                    className={`pagination-button ${pageInfo.page === i ? 'active' : ''}`}
                >
                    {i + 1}
                </button>
            );
        }
        return pages;
    };

    if (isLoading) {
        return <LoadingSpinner/>
    }
    if (isError) {
        return <ErrorComponent error={error}/>
    }
    if (!auctions || auctions.content.length === 0) {
        return <EmptyComponent content="검색된 결과가 없습니다."/>
    }

    return (
        <div className="main-container">
            <h2>세부 조건에 따른 검색 결과</h2>
            <hr/>
            <br/>
            <div className="auction-list">
                {auctions.content.map(auction => (
                    <div key={auction.id} className="auction-card" onClick={() => handleGoToBidPage(auction.id, auction.status.toString())}>
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
                    onClick={() => handlePageChange(currentPage - 1)} 
                    disabled={currentPage === 0}
                    className="pagination-button"
                >
                    이전
                </button>

                {renderPageNumbers()}

                <button 
                    onClick={() => handlePageChange(currentPage + 1)} 
                    disabled={currentPage === pageInfo.totalPages - 1}
                    className="pagination-button"
                >
                    다음
                </button>
            </div>
        </div>
    );
}

export default SearchPage;