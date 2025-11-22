import React from 'react';
import { CurrentBiddingCount } from '../../../../types/dto/response/dashboard';

interface Props {
    count: number;
}

const CurrentBiddingCard: React.FC<Props> = ({ count }) => {
    return (
        <div className="dashboard-card bidding-card">
            <h2 className="card-title" style={{ color: 'white' }}>
                현재 입찰 중인 경매
            </h2>
            <div className="stat-value">{count}</div>
            <div className="stat-label">건</div>
        </div>
    );
};

export default CurrentBiddingCard;