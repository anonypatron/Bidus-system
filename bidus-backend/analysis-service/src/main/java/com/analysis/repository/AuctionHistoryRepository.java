package com.analysis.repository;

import com.analysis.entity.AuctionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AuctionHistoryRepository extends JpaRepository<AuctionHistory, Long> {
    @Query(value = "SELECT * FROM auction_history ah " +
            "WHERE ah.end_time < :now " +
            "AND ah.bid_history_graph IS NULL",
            nativeQuery = true)
    List<AuctionHistory> findEndedAuctionsWithoutGraphData(@Param("now") Instant now);
    Optional<AuctionHistory> findByAuctionId(Long auctionId);
    List<AuctionHistory> findByAuctionIdIn(List<Long> auctionIds);
}
