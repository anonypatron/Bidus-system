import { ErrorComponentProps } from "../../../types/others/error";

export const ErrorComponent = ({ error }: ErrorComponentProps) => {
    return (
        <div className="status-container">
            <div className="status-icon">⚠️</div>
            <h2 className="status-message">오류가 발생했습니다</h2>
            {error && (
                <div className="error-details">
                <strong>Error:</strong> {error.message}
                </div>
            )}
        </div>
    );
};
