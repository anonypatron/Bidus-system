package com.common.dto.auction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuctionDeletedEvent {

    private Long auctionId;

    @Builder
    public AuctionDeletedEvent(Long auctionId) {
        this.auctionId = auctionId;
    }

}
