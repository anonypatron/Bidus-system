'use client';

import { useSearchParams } from "next/navigation";
import { Suspense, useEffect, useState } from "react";
import { BidForm } from '../../../types/dto/request/auction';
import { Auction, AuctionStatus } from '../../../types/dto/response/auction';
import axiosInstance from '../utils/axiosInstance';
import { formatDateTime } from '../utils/formatDataTime';
import { toast } from '../../lib/toast';

const IMAGE_BASE_URL = process.env.NEXT_PUBLIC_IMAGE_DOMAIN;

function AuctionContent() {
    const searchParams = useSearchParams();
    const auctionId = searchParams.get('id');
    
    const [error, setError] = useState<string>('');
    const [customAmountToAdd, setCustomAmountToAdd] = useState<number>(0);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [myBidAmount, setMyBidAmount] = useState<number>(0);
    const [bidForm, setBidForm] = useState<BidForm>({
        auctionId: Number(auctionId),
        price: 0,
    });
    const [auctionDetail, setAuctionDetail] = useState<Auction>({
        id: 0,
        sellerUserName: '',
        imagePath: '',
        title: '',
        description: '',
        categories: [],
        startPrice: 0,
        currentPrice: 0,
        startTime: '',
        endTime: '',
        status: AuctionStatus.IN_PROGRESS,
        bookmarked: false,
    });
    
    useEffect(() => {
        const eventSource = new EventSource(`/api/notifications/${auctionId}/subscribe`);
        const fetchAuction = async () => {
            try {
                const res = await axiosInstance.get(`/auctions/${auctionId}`);
                setAuctionDetail(res.data);
            } catch(err: any) {
                console.log(err);
            } finally {
                setIsLoading(false);
            }
        };
        
        fetchAuction();

        // 입찰 성공
        eventSource.addEventListener('price-update', (event) => {
            const priceData = JSON.parse(event.data);

            setAuctionDetail(prev => ({
                ...prev,
                currentPrice: priceData.price,
            }));
            setError('');

            toast.success(`${priceData.price}원 입찰을 성공했습니다.`);
        });

        // 입찰 실패
        eventSource.addEventListener('bid-failed', (event) => {
            toast.error(event.data);
        });

        // 에러 났을 때 sse닫기
        eventSource.onerror = err => {
            eventSource.close();
        };

        // 창 닫을 때 sse닫기
        return () => {
            eventSource.close();
        };
    }, [auctionId]);

    const handleChangeBidPrice = (price: number) => {
        const newBidPrice = myBidAmount + price;
        
        if (newBidPrice <= 0) {
            return;
        }
        if (newBidPrice <= auctionDetail.currentPrice) {
            setError('현재가보다 높은 금액을 제시해야 합니다.');
            return;
        }
        setMyBidAmount(prev => prev + price);
        setBidForm(prev => ({ ...prev, price: newBidPrice }));
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;
        setCustomAmountToAdd(Number(newValue));
    };

    const handleChangeBidZero = () => {
        setCustomAmountToAdd(0);
    };

    const handleBidSubmit = async () => {
        if (myBidAmount <= 0 || myBidAmount <= auctionDetail.currentPrice)  {
            setError('현재가보다 높은 금액을 제시해야 합니다.');
            return;
        }

        try {
            const res = await axiosInstance.post(`/bids`, bidForm);
        } catch(err: any) {
            alert(err.message);
        }
    };

    if (isLoading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p className="loading-text">로딩 중</p>
            </div>
        );
    }

    if (!auctionDetail) {
        return (
            <div className="loading-container">경매 정보를 찾을 수 없습니다.</div>
        )
    }

    return (
        <div className="auction-container">
            <div className="auction-image-wrapper">
                <img src={`${IMAGE_BASE_URL}${auctionDetail.imagePath}`} alt={auctionDetail.title} className="auction-image" />
            </div>
            <div className="auction-details-wrapper">
                <h1 className="auction-title">{auctionDetail.title}</h1>
                <p className="auction-description">{auctionDetail.description}</p>

                <div className="auction-meta">
                <div><strong>판매자:</strong> {auctionDetail.sellerUserName}</div>
                <div>
                    <strong>카테고리:</strong>
                    {auctionDetail.categories.map((category, index) => (
                    <span key={index} className="category-tag">
                        {category}
                    </span>
                    ))}
                </div>
                </div>

                <div className="auction-pricing">
                    <div className="price-item">
                        <span className="price-label">시작가</span>
                        <span className="price-value">{auctionDetail.startPrice.toLocaleString()}원</span>
                    </div>
                    <div className="price-item">
                        <span className="price-label">현재가</span>
                        <span className="price-value current-price">{auctionDetail.currentPrice.toLocaleString()}원</span>
                    </div>
                    <div className="price-item">
                        <span className="price-label">시작 시간</span>
                        <span className="price-value start-time">{formatDateTime(auctionDetail.startTime)}</span>
                    </div>
                    <div className="price-item">
                        <span className="price-label">종료 시간</span>
                        <span className="price-value end-time">{formatDateTime(auctionDetail.endTime)}</span>
                    </div>
                </div>

                {/* 입찰 영역 */}
                <div className="bidding-section">
                    <div className="bid-controls">
                        <button onClick={() => handleChangeBidPrice(5000)}>+5천</button>
                        <button onClick={() => handleChangeBidPrice(10000)}>+1만</button>
                        <button onClick={() => handleChangeBidPrice(50000)}>+5만</button>
                        <button onClick={() => handleChangeBidPrice(100000)}>+10만</button>
                    </div>
                    <div className="bid-controls">
                        <button onClick={() => handleChangeBidPrice(-5000)}>-5천</button>
                        <button onClick={() => handleChangeBidPrice(-10000)}>-1만</button>
                        <button onClick={() => handleChangeBidPrice(-50000)}>-5만</button>
                        <button onClick={() => handleChangeBidPrice(-100000)}>-10만</button>
                    </div>
                    <div className="direct-bid-input">
                        <span className="input-label">직접 입력</span>
                        <input
                            type="number"
                            className="bid-input-field"
                            value={customAmountToAdd}
                            onChange={handleInputChange}
                            placeholder="입찰할 금액을 입력하세요"
                        />
                        <button
                            className="confirm-bid-button"
                            onClick={() => handleChangeBidPrice(Number(customAmountToAdd))}
                        >
                            추가
                        </button>
                        <button
                            className="bid-init-button"
                            onClick={handleChangeBidZero}
                        >
                            초기화
                        </button>
                    </div>

                    {error &&
                        <div className="error-message">
                            {error}
                        </div>
                    }
                    
                    <div className="current-bid-input">
                        <strong>내 입찰가:</strong>
                        <span>{myBidAmount.toLocaleString()}원</span>
                    </div>

                    <button onClick={handleBidSubmit} className="submit-bid-button">
                        입찰하기
                    </button>
                </div>
            </div>
        </div>
    );
}

function AuctionsPage() {
    return (
        <Suspense fallback={<div className="auction-container">로딩 중...</div>}>
            <AuctionContent/>
        </Suspense>
    );
}

export default AuctionsPage;