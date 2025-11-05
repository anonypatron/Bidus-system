package com.bidding.repository;

import com.bidding.entity.AuctionBidInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionBidInfoRepository extends JpaRepository<AuctionBidInfo, Long> {
}
