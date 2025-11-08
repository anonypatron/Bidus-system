'use client';

import { useQueryClient } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { useAuctionHistoryQueries } from '../../hooks/useAuctionsQuery';
import { useDeleteAuction } from '../../hooks/useDeleteAuctionQuery';
import { AuctionRow } from '../components/AuctionRow';
import AuctionUpdateModal from '../components/modal/AuctionUpdateModal';
import { NoDataRow } from "../components/NoDataRow";

function LibraryPage() {
    const router = useRouter();
    const queryClient = useQueryClient();

    const {
        sellerClosedAuctions,
        sellerInprogressAuctions,
        sellerScheduledAuctions,
        winnerClosedAuctions,
        isLoading,
        isError,
    } = useAuctionHistoryQueries();

    const { mutate: deleteAuction } = useDeleteAuction();
    const [showAuctionUpdateModal, setShowAuctionUpdateModal] = useState<boolean>(false);
    const [selectedModalAuctionId, setSelectedModalAuctionId] = useState<number>(0);
    const [selectedCompareIds, setSelectedCompareIds] = useState(new Set<number>());

    const handleDeleteAuction = async (id: number) => {
        if (confirm('정말 삭제하시겠습니까?')) {
            deleteAuction(id);
        };
    };

    const handleOpenUpdateModal = (id: number) => {
        setSelectedModalAuctionId(id);
        setShowAuctionUpdateModal(true);
    };

    const handleCloseupModal = () => {
        setShowAuctionUpdateModal(false);
        setSelectedModalAuctionId(0);
    };

    const handleCheckBoxChange = (id: number) => {
        setSelectedCompareIds(prev => {
            const newIds = new Set(prev);
            if (newIds.has(id)) {
                newIds.delete(id);
            }
            else {
                newIds.add(id);
            }
            return newIds;
        });
    };

    const handleCompareClick = () => {
        if (selectedCompareIds.size < 2) {
            alert('비교할 항목을 2개 이상 선택해주세요.');
            return;
        }
        const params = new URLSearchParams();
        selectedCompareIds.forEach(id => {
            params.append('id', id.toString());
        });

        router.push(`/compare?${params.toString()}`);
    }

    const handleGotoAuctionDetail = (auctionId: number) => {
        router.push(`/auctions/detail?id=${auctionId}`);
    };

    if (isLoading) {
        return (
            <div className="loading-container">
                <div className="spinner"></div>
                <p className="loading-text">로딩 중</p>
            </div>
        );
    }

    if (isError) {
        return <div>데이터를 불러오는데 실패했습니다.</div>
    }

    return (
        <div className="history-container">
            <div className="history-section">
                <p className="section-title">판매 현황</p>
                <div className="auction-group">
                    <h3 className="group-title">예정된 경매 ({sellerScheduledAuctions.length})</h3>
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th style={{ width: '5%' }}>번호</th>
                                    <th style={{ width: '25%' }}>제목</th>
                                    <th style={{ width: '10%' }}>판매자</th>
                                    <th style={{ width: '25%' }}>경매 기간</th>
                                    <th style={{ width: '10%' }}>상태</th>
                                    <th style={{ width: '10%' }}>최종가</th>
                                    <th colSpan={2} style={{ width: '15%' }}>관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {sellerScheduledAuctions.length > 0 ? (
                                    sellerScheduledAuctions.map((auction, idx) => (
                                        <AuctionRow
                                            key={auction.id}
                                            auction={auction} index={idx} type="scheduled"
                                            onUpdateClick={handleOpenUpdateModal}
                                            onDeleteClick={handleDeleteAuction}
                                            {...{ onDetailClick: () => {}, onCheckboxChange: () => {}, isChecked: false }}
                                        />
                                    ))
                                ) : (
                                    <NoDataRow message="예정된 경매가 없습니다." colSpan={8} />
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="auction-group">
                    <h3 className="group-title">진행중인 경매 ({sellerInprogressAuctions.length})</h3>
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th style={{ width: '5%' }}>번호</th>
                                    <th style={{ width: '25%' }}>제목</th>
                                    <th style={{ width: '10%' }}>판매자</th>
                                    <th style={{ width: '25%' }}>경매 기간</th>
                                    <th style={{ width: '10%' }}>상태</th>
                                    <th style={{ width: '10%' }}>최종가</th>
                                    <th colSpan={2} style={{ width: '15%' }}>관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {sellerInprogressAuctions.length > 0 ? (
                                    sellerInprogressAuctions.map((auction, idx) => (
                                        <AuctionRow
                                            key={auction.id}
                                            auction={auction} index={idx} type="inprogress"
                                            {...{ onUpdateClick: () => {}, onDeleteClick: () => {}, onDetailClick: () => {}, onCheckboxChange: () => {}, isChecked: false }}
                                        />
                                    ))
                                ) : (
                                    <NoDataRow message="진행중인 경매가 없습니다." colSpan={8} />
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className="auction-group">
                    <h3 className="group-title">종료된 경매 ({sellerClosedAuctions.length})</h3>
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th style={{ width: '5%' }}>번호</th>
                                    <th style={{ width: '25%' }}>제목</th>
                                    <th style={{ width: '10%' }}>판매자</th>
                                    <th style={{ width: '25%' }}>경매 기간</th>
                                    <th style={{ width: '10%' }}>상태</th>
                                    <th style={{ width: '10%' }}>최종가</th>
                                    <th colSpan={2} style={{ width: '15%' }}>관리</th>
                                </tr>
                            </thead>
                            <tbody>
                                {sellerClosedAuctions.length > 0 ? (
                                    sellerClosedAuctions.map((auction, idx) => (
                                        <AuctionRow
                                            key={auction.id}
                                            auction={auction} index={idx} type="closed"
                                            {...{ onUpdateClick: () => {}, onDeleteClick: () => {}, onDetailClick: () => {}, onCheckboxChange: () => {}, isChecked: false }}
                                        />
                                    ))
                                ) : (
                                    <NoDataRow message="종료된 경매가 없습니다." colSpan={8} />
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div className="history-section">
                <p className="section-title">낙찰 현황</p>
                <div className="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th style={{ width: '5%' }}>선택</th>
                                <th style={{ width: '5%' }}>번호</th>
                                <th style={{ width: '25%' }}>제목</th>
                                <th style={{ width: '10%' }}>판매자</th>
                                <th style={{ width: '25%' }}>경매 기간</th>
                                <th style={{ width: '10%' }}>상태</th>
                                <th style={{ width: '10%' }}>낙찰가</th>
                                <th colSpan={2} style={{ width: '10%' }}>관리</th>
                            </tr>
                        </thead>
                        <tbody className="tbody-group">
                            {winnerClosedAuctions.length > 0 ? (
                                winnerClosedAuctions.map((auction, idx) => (
                                    <AuctionRow
                                        key={auction.id}
                                        auction={auction}
                                        index={idx}
                                        type="winner"
                                        onDetailClick={handleGotoAuctionDetail}
                                        onCheckboxChange={handleCheckBoxChange}
                                        isChecked={selectedCompareIds.has(auction.id)}
                                        {...{ onUpdateClick: () => {}, onDeleteClick: () => {} }}
                                    />
                                ))
                            ) : (
                                <NoDataRow message="낙찰된 경매가 없습니다." colSpan={9} />
                            )}
                        </tbody>
                    </table>
                </div>
                {selectedCompareIds.size > 0 && (
                    <div className="compare-sticky-footer">
                        <p>{selectedCompareIds.size}개 항목 선택됨</p>
                        <button onClick={handleCompareClick} className="compare-button">
                            비교하기
                        </button>
                    </div>
                )}
            </div>
            {showAuctionUpdateModal && 
                <AuctionUpdateModal 
                    auctionId = {selectedModalAuctionId}
                    onClose = {handleCloseupModal}
                    onUpdateSuccess = {() => {
                        queryClient.invalidateQueries({ queryKey: ['auctionHistory']})
                    }}
                />
            }
        </div>
    );
}

export default LibraryPage;