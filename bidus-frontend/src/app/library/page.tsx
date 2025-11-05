'use client';

import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useAuctionHistoryQueries } from '../../hooks/useAuctionsQuery';
import { useDeleteAuction } from '../../hooks/useDeleteAuctionQuery';
import AuctionUpdateModal from '../components/modal/AuctionUpdateModal';
import { formatDateTime } from '../utils/formatDataTime';
import { useRouter } from "next/navigation";

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
                    <h3 className="group-title">예정된 경매</h3>
                    {sellerScheduledAuctions.length !== 0 ? (
                        <div className="table-wrapper">
                            <table>
                                <tbody>
                                    {sellerScheduledAuctions.map((auction, idx) => {
                                        return (
                                            <tr key={auction.id}>
                                                <td>{idx + 1}</td>
                                                <td>{auction.title}</td>
                                                <td>{auction.sellerUserName}</td>
                                                {/* <td>{auction.startPrice.toLocaleString()}원</td> */}
                                                <td>{formatDateTime(auction.startTime)} ~ {formatDateTime(auction.endTime)}</td>
                                                <td><span className={`status-badge`}>{auction.status}</span></td>
                                                <td>{auction.finalPrice ? auction.finalPrice.toLocaleString() + '원' : '미정'}</td>
                                                <td><button onClick={() => handleOpenUpdateModal(auction.id)} className="update-button">수정</button></td>
                                                <td><button onClick={() => handleDeleteAuction(auction.id)} className="delete-button">삭제</button></td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="no-data-message"><p>예정된 경매가 없습니다.</p></div>
                    )}
                </div>

                <div className="auction-group">
                    <h3 className="group-title">진행중인 경매</h3>
                    {sellerInprogressAuctions.length !== 0 ? (
                        <div className="table-wrapper">
                            <table>
                                <tbody>
                                    {sellerInprogressAuctions.map((auction, idx) => {
                                        return (
                                            <tr key={auction.id}>
                                                <td>{idx + 1}</td>
                                                <td>{auction.title}</td>
                                                <td>{auction.sellerUserName}</td>
                                                {/* <td>{auction.startPrice.toLocaleString()}원</td> */}
                                                <td>{formatDateTime(auction.startTime)} ~ {formatDateTime(auction.endTime)}</td>
                                                <td><span className={`status-badge`}>{auction.status}</span></td>
                                                <td>{auction.finalPrice ? auction.finalPrice.toLocaleString() + '원' : '미정'}</td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="no-data-message"><p>진행중인 경매가 없습니다.</p></div>
                    )}
                </div>

                <div className="auction-group">
                    <h3 className="group-title">종료된 경매</h3>
                    {sellerClosedAuctions.length !== 0 ? (
                        <div className="table-wrapper">
                            <table>
                                <tbody>
                                    {sellerClosedAuctions.map((auction, idx) => {
                                        return (
                                            <tr key={auction.id}>
                                                <td>{idx + 1}</td>
                                                <td>{auction.title}</td>
                                                <td>{auction.sellerUserName}</td>
                                                {/* <td>{auction.startPrice.toLocaleString()}원</td> */}
                                                <td>{formatDateTime(auction.startTime)} ~ {formatDateTime(auction.endTime)}</td>
                                                <td><span className={`status-badge`}>{auction.status}</span></td>
                                                <td>{auction.finalPrice ? auction.finalPrice.toLocaleString() + '원' : '유찰'}</td>
                                            </tr>
                                        );
                                    })}
                                </tbody>
                            </table>
                        </div>
                    ) : (
                        <div className="no-data-message"><p>완료된 기록이 없습니다.</p></div>
                    )}
                </div>
            </div>

            <div className="history-section">
                <p className="section-title">낙찰 현황</p>
                {winnerClosedAuctions.length !== 0 ? (
                    <div className="table-wrapper">
                        <table>
                            <tbody>
                                {winnerClosedAuctions.map((auction, idx) => (
                                    <tr key={auction.id}>
                                        <td>
                                            <input
                                                type="checkbox"
                                                checked={selectedCompareIds.has(auction.id)}
                                                onChange={() => handleCheckBoxChange(auction.id)}
                                            />
                                        </td>
                                        <td>{idx + 1}</td>
                                        <td>{auction.title}</td>
                                        <td>{auction.sellerUserName}</td>
                                        <td>{formatDateTime(auction.startTime)} ~ {formatDateTime(auction.endTime)}</td>
                                        <td><span className={`status-badge`}>{auction.status}</span></td>
                                        <td>{auction.finalPrice?.toLocaleString()}원</td>
                                        <td><button onClick={() => handleGotoAuctionDetail(auction.id)} className="update-button">기록 보기</button></td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                ) : (
                    <div className="no-data-message">
                        <p>낙찰된 경매가 없습니다.</p>
                    </div>
                )}
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