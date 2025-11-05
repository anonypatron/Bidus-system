package com.analysis.repository;

import com.analysis.entity.BidHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BidHistoryRepository extends JpaRepository<BidHistory, Long> {
    List<BidHistory> findAllByAuctionIdOrderByBidTimeAsc(Long auctionId);
}
