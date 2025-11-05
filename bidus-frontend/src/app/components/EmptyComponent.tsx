export default function EmptyComponent() {
    return (
        <div className="status-container">
        <div className="status-icon">📭</div>
        <h2 className="status-message">표시할 데이터가 없습니다</h2>
        <p>선택한 경매 기록이 없거나, 불러올 수 없습니다.</p>
        </div>
    );
}