package com.auction.repository;

import com.auction.dto.response.CategoryStatsDto;
import com.auction.entity.Auction;
import com.common.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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

    List<Auction> findCurrentBiddingByUserId(Long id);

    Optional<Auction> findByIdWithCategories(Long id);

    // auction stats
    Long findSellCountThisMonth(Long id);
    Long findWinCountThisMonth(Long id);

    Long findCurrentBiddingCount(Long id);

    List<CategoryStatsDto> findTop10Categories(Long id);

}
