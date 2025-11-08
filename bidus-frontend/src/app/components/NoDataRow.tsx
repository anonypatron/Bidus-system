export const NoDataRow = ({ message, colSpan }: { message: string, colSpan: number }) => (
    <tr>
        <td colSpan={colSpan} className="no-data-cell">
            {message}
        </td>
    </tr>
);