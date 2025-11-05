package com.auction.repository;

import com.auction.entity.AuctionBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuctionBookmarkRepository extends JpaRepository<AuctionBookmark, Long> {
/*
    @Query("SELECT b.auction.id " +
            "FROM AuctionBookmark b " +
            "WHERE b.user.id = :userId")
    Set<Long> findAuctionIdsByUserId(
            @Param("userId") Long userId
    );

    @Query("SELECT b.auction.id " +
            "fROM AuctionBookmark b " +
            "WHERE b.user.id = :userId AND b.auction.id IN :auctionIds")
    Set<Long> findBookmarkedAuctionIdsByUserIdAndAuctionIds(
            @Param("userId") Long userId,
            @Param("auctionIds") List<Long> auctionIds
    );

    @Query(value = "SELECT b.auction " +
            "FROM AuctionBookmark AS b " +
            "LEFT JOIN FETCH b.auction.auctionCategories AS ac " +
            "LEFT JOIN FETCH  ac.category " +
            "WHERE b.user.id = :userId AND b.auction.status = :status",
    countQuery = "SELECT COUNT(b)" +
            "FROM AuctionBookmark b " +
            "WHERE b.user.id = :userId AND b.auction.status = :status")
    Page<Auction> findBookmarkedAuctionsByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") AuctionStatus status,
            Pageable pageable
    );
*/
    boolean existsByUserIdAndAuctionId(Long userId, Long auctionId);

    Optional<AuctionBookmark> findByUserIdAndAuctionId(Long userId, Long auctionId);

}
