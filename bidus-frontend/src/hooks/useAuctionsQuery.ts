import { fetchAllInprogressAuctions, fetchAuctionHistory, deleteAuction } from "@/api/auctions"
import { useQueries, useQuery } from "@tanstack/react-query"

export const useAuctionQuery = (currentPage: number) => {
    return useQuery({
        queryKey: ['auctions', currentPage],
        queryFn: () => fetchAllInprogressAuctions(currentPage),
        staleTime: 1000 * 60 * 5,
    });
};

// 필터 타입
interface AuctionHistoryFilters {
    role: 'seller' | 'winner';
    status: 'CLOSED' | 'IN_PROGRESS' | 'SCHEDULED';
};

export const AUCTION_HISTORY_KEYS = {
    all: ['auctionHistory'] as const,
    lists: () => [...AUCTION_HISTORY_KEYS.all, 'lists'] as const,
    list: (filters: AuctionHistoryFilters) => [...AUCTION_HISTORY_KEYS.lists(), filters] as const,
};

export const useAuctionHistoryQueries = () => {

    const sellerClosed = { role: 'seller', status: 'CLOSED' } as const;
    const sellerInProgress = { role: 'seller', status: 'IN_PROGRESS' } as const;
    const sellerScheduled = { role: 'seller', status: 'SCHEDULED' } as const;
    const winnerClosed = { role: 'winner', status: 'CLOSED' } as const;

    const results = useQueries({
        queries: [
            {
                queryKey: AUCTION_HISTORY_KEYS.list(sellerClosed),
                queryFn: () => fetchAuctionHistory('seller', 'CLOSED'),
            },
            {
                queryKey: AUCTION_HISTORY_KEYS.list(sellerInProgress),
                queryFn: () => fetchAuctionHistory('seller', 'IN_PROGRESS'),
            },
            {
                queryKey: AUCTION_HISTORY_KEYS.list(sellerScheduled),
                queryFn: () => fetchAuctionHistory('seller', 'SCHEDULED'),
            },
            {
                queryKey: AUCTION_HISTORY_KEYS.list(winnerClosed),
                queryFn: () => fetchAuctionHistory('winner', 'CLOSED'),
            },
        ],
    });

    const isLoading = results.some(query => query.isPending);
    const isError = results.some(query => query.isError);

    return {
        sellerClosedAuctions: results[0].data ?? [],
        sellerInprogressAuctions: results[1].data ?? [],
        sellerScheduledAuctions: results[2].data ?? [],
        winnerClosedAuctions: results[3].data ?? [],
        isLoading,
        isError,
    };
};