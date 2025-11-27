'use client';

import { LoadingSpinner } from '@/app/components/others/LoadingSpinner';
import { useSearchParams } from 'next/navigation';
import { useAnalysisQuery } from '../../../hooks/useAuctionAnalysis';
import AuctionDetailGraph from '../../components/graph/AuctionAnalysisGraph';
import { ErrorComponent } from '../../components/others/ErrorComponent';
import { Suspense } from 'react';

function AuctionDetailContent() {
    const searchParams = useSearchParams();
    const auctionId = searchParams.get('id');

    const { data: auctionAnalysis, isLoading, isError, error } = useAnalysisQuery(Number(auctionId));

    if (isLoading) {
        return <LoadingSpinner text='데이터를 불러오는 중...'/>
    }

    if (isError) {
        return <ErrorComponent error={error}/>
    }

    if (!auctionAnalysis) {
        return (
            <div className="graph-placeholder-container">
                <p className="graph-placeholder-text">
                    입찰 기록이 없어 그래프를 표시할 수 없습니다.
                </p>
            </div>
        );
    } 

    console.log(auctionAnalysis);
    
    return (
        <div className="auction-detail-page">
            <div className="auction-detail-header">
                <h1 className="auction-detail-title">
                    {auctionAnalysis.title} 입찰 시각화
                </h1>
            </div>
            <div className="auction-graph-container">
                <AuctionDetailGraph { ...auctionAnalysis }/>
            </div>
        </div>
    );
}

function AuctionDetailPage() {
    return (
        <Suspense fallback={<div className='auction-detail-page'>로딩 중...</div>}>
            <AuctionDetailContent/>
        </Suspense>
    );
}

export default AuctionDetailPage;