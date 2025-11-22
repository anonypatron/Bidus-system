package com.auction.repository;

import com.auction.entity.Auction;
import com.common.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryCustom {
    Page<Auction> findByIdInAndStatus(Set<Long> ids, AuctionStatus status, Pageable pageable);
}
