package com.common.dto.auction;

import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AuctionStartedEvent {

    private Long auctionId;
    private AuctionStatus status;

    @Builder
    public AuctionStartedEvent(
            Long auctionId,
            AuctionStatus status
    ) {
        this.auctionId = auctionId;
        this.status = status;
    }

}
