package com.common.dto.bid;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BidFailedEvent {

    private Long auctionId;
    private Long userId;
    private String reason;

    @Builder
    public BidFailedEvent(Long auctionId, Long userId, String reason) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.reason = reason;
    }

}
