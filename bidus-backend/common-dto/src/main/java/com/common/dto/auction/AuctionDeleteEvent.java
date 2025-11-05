package com.common.dto.auction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuctionDeleteEvent {

    private Long auctionId;

    @Builder
    public AuctionDeleteEvent(Long auctionId) {
        this.auctionId = auctionId;
    }

}
