'use client';

import {
    LuBell,
    LuCreditCard,
    LuPackageCheck,
    LuCircle
} from "react-icons/lu";

type DashboardSummary = {
    paymentPendingCount: number;
    shippingRequiredCount: number;
    supportReplyCount: number;
};

function DashboardPage() {
    // fetch로 가져오기
    const summary = {
        paymentPendingCount: 0,
        shippingRequiredCount: 0,
        supportReplyCount: 0,
    };

    return (
        <div className="dashboard-container">
            <div className="dashboard-grid top-summary">
                <div className="summary-card todo-card">
                    <LuCreditCard className="card-icon" />
                    <div className="card-content">
                        <span className="card-label">결제 대기</span>
                        <span className="card-value">
                            {summary.paymentPendingCount}건
                        </span>
                    </div>
                </div>
            </div>

            {/* --- 3. 메인 그리드 (판매/구매/알림) --- */}
            <div className="dashboard-grid main-content">
                {/* 판매 현황 */}
                <div className="dashboard-widget sales-widget">
                    <h3 className="widget-title">나의 판매 현황</h3>
                    <div className="widget-content">
                        <p>이번 달 판매액: ${}원</p>
                        {/* ... 진행 중인 경매 목록 (컴포넌트) ... */}
                        <a href="/library" className="widget-more-link">
                            판매 내역 더보기 &rarr;
                        </a>
                    </div>
                </div>

                {/* 구매 현황 */}
                <div className="dashboard-widget bidding-widget">
                    <h3 className="widget-title">나의 입찰 현황</h3>
                    <div className="widget-content">
                        <p>현재 3개 상품에 입찰 중입니다.</p>
                        {/* ... 입찰 중인 상품 목록 (컴포넌트) ... */}
                        <a href="/library" className="widget-more-link">
                            입찰 내역 더보기 &rarr;
                        </a>
                    </div>
                </div>

                {/* 최근 알림 */}
                <div className="dashboard-widget notification-widget">
                    <h3 className="widget-title">
                        최근 알림 <LuBell />
                    </h3>
                    <ul className="notification-list">
                        <li>'빈티지 카메라'에 새 입찰 (방금 전)</li>
                        <li>'LG 그램' 낙찰! (20분 전)</li>
                        <li>'나이키 조던' 경매 10분 전 (1시간 전)</li>
                    </ul>
                </div>

                {/* 관심 상품 */}
                <div className="dashboard-widget bookmark-widget">
                    <h3 className="widget-title">마감 임박 관심 상품</h3>
                    <div className="widget-content">
                        {/* ... 즐겨찾기 목록 (컴포넌트) ... */}
                        <a href="/bookmark" className="widget-more-link">
                            즐겨찾기 더보기 &rarr;
                        </a>
                    </div>
                </div>

                {/* 빠른 링크 */}
                <div className="dashboard-widget quick-links">
                    <button className="quick-link-btn">
                        <LuCircle /> 상품 등록하기
                    </button>
                    <button className="quick-link-btn">
                        <LuCircle /> 1:1 문의하기
                    </button>
                </div>
            </div>
        </div>
    );
}

export default DashboardPage;
