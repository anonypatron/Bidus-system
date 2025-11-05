package com.common.dto.auction;

import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuctionClosedEvent {

    private Long auctionId;
    private Long winnerId;
    private Long finalPrice;
    private AuctionStatus status;

    @Builder
    public AuctionClosedEvent(Long auctionId, Long winnerId, Long finalPrice, AuctionStatus status) {
        this.auctionId = auctionId;
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
        this.status = status;
    }

}
