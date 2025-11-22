'use client';

import { useSearchParams } from "next/navigation";
import { useAnalyzesQuery } from '../../hooks/useAuctionAnalysis';
import AuctionAnalysisGraphs from '../components/graph/AuctionAnalysisGraphs';
import AuctionKeyIndicator from '../components/indicator/AuctionKeyIndicator';
import { LoadingSpinner } from "../components/others/LoadingSpinner";
import { ErrorComponent } from "../components/others/ErrorComponent";
import EmptyComponent from "../components/others/EmptyComponent";
import { useRouter } from "next/navigation";

const COLORS = ['#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#387908', '#e6194B'];

function ComparePage() {
    const searchParams = useSearchParams();
    const router = useRouter();

    const ids: Array<number> = searchParams.getAll('id').map(id => Number(id));
    const { data: auctionHistory, isLoading, isError, error } = useAnalyzesQuery(ids);

    if (isLoading) {
        return <LoadingSpinner/>;
    }

    if (isError) {
        return <ErrorComponent error={error}/>;
    }

    if (!auctionHistory || auctionHistory.length === 0) {
        return <EmptyComponent/>
    }

    console.log(auctionHistory);
    
    return (
        <div className="compare-page-container">
            <header className="compare-header">
                <button className="back-button" onClick={() => router.back()}>
                    &larr;
                </button>
                <h1>경매 비교 ({auctionHistory.length}건)</h1>
            </header>

            <main>
                <section className="compare-main-section">
                    <h2>가격 변동 그래프</h2>
                    <AuctionAnalysisGraphs auctionHistory={auctionHistory} colors={COLORS}/>
                </section>

                <section className="compare-main-section">
                    <h2>핵심 지표</h2>
                    <AuctionKeyIndicator auctionHistory={auctionHistory} colors={COLORS} />
                </section>
            </main>
        </div>
    );
};

export default ComparePage;