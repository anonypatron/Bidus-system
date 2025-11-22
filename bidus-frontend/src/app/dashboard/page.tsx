'use client';

import { useDashboardQuery } from '../../hooks/useDashboardStats';
import EmptyComponent from '../components/others/EmptyComponent';
import { ErrorComponent } from '../components/others/ErrorComponent';
import { LoadingSpinner } from '../components/others/LoadingSpinner';
import CategoryRadarChart from '../components/dashboard/CategoryRadarChart';
import CurrentBiddingCard from '../components/dashboard/CurrentBiddingCard';
import MonthlySummaryWidget from '../components/dashboard/MonthlySummaryWidget';

function DashboardPage() {
    const { data: dashboardStats, isLoading, isError, error } = useDashboardQuery();

    if (isLoading) {
        return <LoadingSpinner text='대시보드 데이터를 불러오는 중...'/>
    }

    if (isError) {
        return <ErrorComponent error={error}/>
    }

    if (!dashboardStats) {
        return <EmptyComponent content='데이터가 없습니다.'/>
    }
    
    const { monthlySummary, biddingCount, categories } = dashboardStats;

    // console.log(dashboardStats);

    return (
        <div className="dashboard-container">
            <header className="dashboard-header">
                <h1>대시보드</h1>
            </header>
            <CategoryRadarChart categories={categories} />
            <CurrentBiddingCard count={biddingCount.count} />
            <MonthlySummaryWidget summary={monthlySummary} />
        </div>
    );
}

export default DashboardPage;
