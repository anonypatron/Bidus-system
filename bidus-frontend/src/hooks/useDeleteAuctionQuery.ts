import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteAuction } from '../api/auctions';

export const useDeleteAuction = () => {
    const queryClient = useQueryClient();
    
    return useMutation({
        mutationFn: deleteAuction,
        onSuccess: () => {
            // alert('경매가 성공적으로 삭제되었습니다.');
            queryClient.invalidateQueries({ queryKey: ['auctionHistory'] });
        },
        onError: (error) => {
            console.error('경매 삭제 실패:', error);
            // alert('경매 삭제 중 오류가 발생했습니다.');
        },
    });
};