// import { useMutation, useQueryClient } from "@tanstack/react-query"

// export const useBookmarkToggle = () => {
//   const queryClient = useQueryClient();

//   return useMutation({
//     // mutationFn: API를 호출하는 실제 함수
//     mutationFn: ({ auctionId, isBookmarked }: { auctionId: number; isBookmarked: boolean }) => {
//       // isBookmarked는 현재 상태이므로, 반대 액션을 취해야 합니다.
//       if (isBookmarked) {
//         return deleteBookmark(auctionId);
//       } else {
//         return addBookmark(auctionId);
//       }
//     },
    
//     // ★★★ 낙관적 업데이트(Optimistic Update) 로직 ★★★
//     onMutate: async ({ auctionId }) => {
//       const queryKey = ['auctions', currentPage]; // 현재 쿼리 키

//       // 1. 진행중인 refetch를 취소합니다. (낙관적 업데이트를 덮어쓰지 않도록)
//       await queryClient.cancelQueries({ queryKey });

//       // 2. 현재 캐시된 데이터를 가져옵니다. (롤백을 위해)
//       const previousAuctionsData = queryClient.getQueryData(queryKey);

//       // 3. 캐시 데이터를 수동으로 업데이트합니다.
//       queryClient.setQueryData(queryKey, (oldData: any) => {
//         const newAuctions = oldData.auctions.map((auction: any) => {
//           if (auction.id === auctionId) {
//             return { ...auction, isBookmarked: !auction.isBookmarked };
//           }
//           return auction;
//         });
//         return { ...oldData, auctions: newAuctions };
//       });

//       // 4. 롤백을 위해 이전 데이터를 반환합니다.
//       return { previousAuctionsData };
//     },

//     // 에러 발생 시 onMutate에서 반환된 값으로 롤백합니다.
//     onError: (err, variables, context) => {
//       if (context?.previousAuctionsData) {
//         const queryKey = ['auctions', currentPage];
//         queryClient.setQueryData(queryKey, context.previousAuctionsData);
//       }
//     },

//     // 성공/실패 여부와 관계없이 항상 데이터를 다시 가져와 서버와 상태를 동기화합니다.
//     onSettled: () => {
//       const queryKey = ['auctions', currentPage];
//       queryClient.invalidateQueries({ queryKey });
//     },
//   });
// };