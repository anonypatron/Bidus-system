'use client';

import { AuctionAnalysisDto } from '../../../../types/dto/response/auction';
import { useRef, useState, useEffect } from 'react';

interface Props {
  auctionHistory: AuctionAnalysisDto[];
  colors: string[];
}

const formatKrw = (price: number) => {
  return `${price.toLocaleString()}원`;
};

const ChevronIcon = ({ direction = 'left' }: { direction: 'left' | 'right' }) => (
  <svg
    width="24"
    height="24"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    style={{ transform: direction === 'right' ? 'rotate(180deg)' : 'none' }}
  >
    <polyline points="15 18 9 12 15 6"></polyline>
  </svg>
);

function AuctionKeyIndicator({ auctionHistory, colors }: Props) {
    const scrollRef = useRef<HTMLDivElement>(null);

    const [scrollState, setScrollState] = useState({
        canScrollLeft: false,
        canScrollRight: false,
    });

    const checkScroll = () => {
        if (scrollRef.current) {
            const { scrollLeft, scrollWidth, clientWidth } = scrollRef.current;
            const canScrollLeft = scrollLeft > 1; 
            const canScrollRight = scrollLeft + clientWidth < scrollWidth - 1;

            setScrollState({ canScrollLeft, canScrollRight });
        }
    };

    useEffect(() => {
        const ref = scrollRef.current;
        if (ref) {
            checkScroll(); 
            
            ref.addEventListener('scroll', checkScroll);
            window.addEventListener('resize', checkScroll);

            return () => {
                ref.removeEventListener('scroll', checkScroll);
                window.removeEventListener('resize', checkScroll);
            };
        }
    }, [auctionHistory]);

    const handleScrollClick = (direction: 'left' | 'right') => {
        if (scrollRef.current) {
            const scrollAmount = 320;
            scrollRef.current.scrollBy({ 
                left: direction === 'left' ? -scrollAmount : scrollAmount, 
                behavior: 'smooth' 
            });
        }
    };

    return (
        <div className="indicator-container-flex">
            <button 
                className="scroll-button-flex"
                onClick={() =>handleScrollClick('left')}
                style={{ visibility: scrollState.canScrollLeft ? 'visible' : 'hidden'}}
            >
                <ChevronIcon direction='left'/>
            </button>
            <div className="indicator-wrapper" ref={scrollRef}>
                {auctionHistory.map((auction, index) => (
                    <div className="indicator-card" key={auction.title}>
                    <div
                        className="card-color-stripe"
                        style={{ backgroundColor: colors[index % colors.length] }}
                    />
                    <h3 className="card-title">{auction.title}</h3>
                    
                    <div className="card-metric">
                        <span className="metric-label">시작 가격</span>
                        <span className="metric-value">{formatKrw(auction.startPrice)}</span>
                    </div>
                    <div className="card-metric">
                        <span className="metric-label">최종 낙찰가</span>
                        <span className="metric-value">{formatKrw(auction.finalPrice)}</span>
                    </div>
                    <div className="card-metric">
                        <span className="metric-label">총 입찰 수</span>
                        <span className="metric-value">{auction.totalBidCount.toLocaleString()} 회</span>
                    </div>
                    <div className="card-metric">
                        <span className="metric-label">경매 상태</span>
                        <span className="metric-value">{auction.status}</span>
                    </div>
                    </div>
                ))}
            </div>
            <button
                className="scroll-button-flex"
                onClick={() => handleScrollClick('right')}
                style={{ visibility: scrollState.canScrollRight ? 'visible' : 'hidden'}}
            >
                <ChevronIcon direction='right'/>
            </button>
        </div>
    );
}

export default AuctionKeyIndicator;