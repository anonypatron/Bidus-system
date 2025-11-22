import { Auction } from "../../../types/dto/response/auction";
import { formatDateTime } from "../utils/formatDataTime";

export const AuctionRow = ({ 
    auction, 
    index, 
    type,
    onUpdateClick,
    onDeleteClick,
    onDetailClick,
    onRedirect,
    onCheckboxChange,
    isChecked
}: { 
    auction: Auction, 
    index: number, 
    type: 'scheduled' | 'inprogress' | 'closed' | 'winner' | 'current-bidding',
    onUpdateClick: (id: number) => void,
    onDeleteClick: (id: number) => void,
    onDetailClick: (id: number) => void,
    onRedirect: (id: number) => void,
    onCheckboxChange: (id: number) => void,
    isChecked: boolean
}) => {

    // 타입별 최종 가격 로직
    const renderFinalPrice = () => {
        if (type === 'scheduled' || type === 'inprogress') {
            return '미정';
        }
        if (type === 'closed') {
            return auction.finalPrice ? `${auction.finalPrice.toLocaleString()}원` : '유찰';
        }
        if (type === 'winner') {
            return auction.finalPrice ? `${auction.finalPrice.toLocaleString()}원` : 'N/A';
        }
        if (type === 'current-bidding') {
            return auction.currentPrice ? `${auction.currentPrice.toLocaleString()}원` : '미정';
        }
        return '미정';
    };

    // 타입별 액션 버튼 로직
    const renderActions = () => {
        if (type === 'scheduled') {
            return (
                <>
                    <td><button onClick={() => onUpdateClick(auction.id)} className="update-button">수정</button></td>
                    <td><button onClick={() => onDeleteClick(auction.id)} className="delete-button">삭제</button></td>
                </>
            );
        }
        if (type === 'winner') {
            return (
                <td colSpan={2}><button onClick={() => onDetailClick(auction.id)} className="update-button">기록 보기</button></td>
            );
        }

        if (type === 'current-bidding') {
            return <td><button onClick={() => onRedirect(auction.id)} className="update-button">바로가기</button></td>
        }
        return <td colSpan={2}></td>;
    };

    return (
        <tr key={auction.id}>
            {type === 'winner' && (
                <td>
                    <input
                        type="checkbox"
                        checked={isChecked}
                        onChange={() => onCheckboxChange(auction.id)}
                    />
                </td>
            )}
            <td>{index + 1}</td>
            <td>{auction.title}</td>
            <td>{auction.sellerUserName}</td>
            <td>{formatDateTime(auction.startTime)} ~ {formatDateTime(auction.endTime)}</td>
            <td><span className={`status-badge status-${auction.status}`}>{auction.status}</span></td>
            <td>{renderFinalPrice()}</td>
            {renderActions()}
        </tr>
    );
};
