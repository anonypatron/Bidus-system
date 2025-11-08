package com.common.dto.auction;

import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuctionCanceledEvent {

    private Long auctionId;
    private AuctionStatus status;

    @Builder
    public AuctionCanceledEvent(Long auctionId, AuctionStatus status) {
        this.auctionId = auctionId;
        this.status = status;
    }

}
