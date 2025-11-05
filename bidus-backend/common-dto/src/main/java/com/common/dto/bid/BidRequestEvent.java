package com.common.dto.bid;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class BidRequestEvent {

    private Long auctionId;
    private Long userId;
    private Long price;

    @Builder
    public BidRequestEvent(Long auctionId, Long userId, Long price){
        this.auctionId = auctionId;
        this.userId = userId;
        this.price = price;
    }

}
