'use client';

import LoadingSpinner from '@/app/components/LoadingSpinner';
import { useSearchParams } from 'next/navigation';
import { useAnalysisQuery } from '../../../hooks/useAuctionAnalysis';
import AuctionDetailGraph from '../../components/graph/AuctionAnalysisGraph';

function AuctionDetailPage() {
    const searchParams = useSearchParams();
    const auctionId = searchParams.get('id');

    const { data: auctionAnalysis, isLoading, isError, error } = useAnalysisQuery(Number(auctionId));

    if (isLoading) {
        return <LoadingSpinner text='데이터를 불러오는 중...'/>
    }

    if (isError) {
        return <div>에러가 발생했습니다: {error?.message || 'Unknown error'}</div>;
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

export default AuctionDetailPage;