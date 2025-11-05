package com.common.dto.bid;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@Getter
public class BidPlacedEvent {

    private Long auctionId;
    private Long userId;
    private Long price;
    private Instant bidTime;

    @Builder
    public BidPlacedEvent(
            Long auctionId,
            Long userId,
            Long price,
            Instant bidTime
    ) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.price = price;
        this.bidTime = bidTime;
    }

}
