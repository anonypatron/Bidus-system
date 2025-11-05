package com.analysis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class BidHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long auctionId;
    private Long userId;
    private Long price;

    private Instant bidTime;

    @Builder
    public BidHistory(Long auctionId, Long userId, Long price, Instant endTime, Instant bidTime) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.price = price;
        this.bidTime = bidTime;
    }

}
