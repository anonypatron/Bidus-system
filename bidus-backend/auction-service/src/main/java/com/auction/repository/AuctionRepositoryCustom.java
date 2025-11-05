package com.auction.repository;

import com.auction.entity.Auction;
import com.common.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuctionRepositoryCustom {

    List<Auction> findAllByStatusAndStartTimeBeforeWithCategories(
            AuctionStatus status,
            Instant now
    );

    List<Auction> findAllByStatusAndEndTimeBefore(
            AuctionStatus status,
            Instant now
    );

    Page<Auction> findAllByStatusWithCategories(
            Pageable pageable,
            AuctionStatus status
    );

    List<Auction> findAllByWinnerIdAndStatusWithCategories(
            Long id,
            AuctionStatus status
    );

    List<Auction> findAllBySellerIdAndStatusWithCategories(
            Long id,
            AuctionStatus status
    );

    Optional<Auction> findByIdWithCategories(Long id);

    Set<Long> findAuctionIdsByUserId(Long userId);

    // [추가] 2번 메서드
    Set<Long> findBookmarkedAuctionIdsByUserIdAndAuctionIds(
            Long userId,
            List<Long> auctionIds
    );

    // [추가] 3번 메서드
    Page<Auction> findBookmarkedAuctionsByUserIdAndStatus(
            Long userId,
            AuctionStatus status,
            Pageable pageable
    );

}
