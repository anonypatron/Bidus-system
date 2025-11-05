package com.bidding.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long auctionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long price;

    private Instant bidTime;

    @Builder
    public Bid(Long auctionId, Long userId, Long price) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.price = price;
        this.bidTime = Instant.now();
    }

}
