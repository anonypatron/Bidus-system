import React from 'react';
import { MonthlySummaryDto } from '../../../../types/dto/response/dashboard';

interface Props {
  summary: MonthlySummaryDto;
}

const MonthlySummaryWidget: React.FC<Props> = ({ summary }) => {
    const sellerCount = summary?.sellerCount ?? 0;
    const winCount = summary?.winCount ?? 0;

    return (
        <div className="dashboard-card summary-card">
            <h2 className="card-title">이번 달 활동 요약</h2>
            <div className="summary-grid">
                <div className="summary-item">
                    <div className="stat-value">
                        {sellerCount > 0 ? sellerCount : 0}
                    </div>
                    <div className="stat-label">판매 완료</div>
                </div>
                <div className="summary-item">
                    <div className="stat-value">
                        {winCount > 0 ? winCount : 0}
                    </div>
                    <div className="stat-label">낙찰 성공</div>
                </div>
            </div>
        </div>
    );
};

export default MonthlySummaryWidget;