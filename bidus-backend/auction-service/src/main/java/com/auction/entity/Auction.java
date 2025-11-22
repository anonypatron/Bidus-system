package com.auction.entity;

import com.common.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;
    private String sellerUserName;

    private String imagePath;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<AuctionCategory> auctionCategories = new ArrayList<>();

    @Column(nullable = false)
    private Long startPrice;

    @Column(nullable = false)
    private Long currentPrice;

    private Instant startTime;
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private Long highestBidderId;

    private Long winnerId; // 낙찰자 id
    private Long finalPrice; // 낙찰가

    @Builder
    public Auction(
            Long sellerId,
            String sellerUserName,
            String imagePath,
            String title,
            String description,
            Long startPrice,
            Instant startTime,
            Instant endTime) {
        this.sellerId = sellerId;
        this.sellerUserName = sellerUserName;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.startPrice = startPrice;
        this.currentPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.SCHEDULED;
    }

    public void changeStatus(AuctionStatus status) {
        this.status = status;
    }

    public void updateHighestBid(Long bidderId, Long newPrice) {
        this.highestBidderId = bidderId;
        this.currentPrice = newPrice;
    }

    public void closeAuction() {
        this.status = AuctionStatus.CLOSED;
        if (this.highestBidderId != null) {
            this.winnerId = this.highestBidderId;
            this.finalPrice = this.currentPrice;
        }
    }

    public void addCategory(Category category) {
        AuctionCategory auctionCategory = AuctionCategory.create(this, category);
        this.auctionCategories.add(auctionCategory);
    }

}
