package com.common.dto.auction;

import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
public class AuctionStartedEvent {

    private Long auctionId;
    private String title;
    private String description;
    private String imagePath;
    private String sellerUserName;
    private Long startPrice;
    private Instant startTime;
    private Instant endTime;
    private AuctionStatus status;
    private List<String> categories;

    @Builder
    public AuctionStartedEvent(
            Long auctionId,
            String title,
            String description,
            String imagePath,
            String sellerUserName,
            Long startPrice,
            Instant startTime,
            Instant endTime,
            AuctionStatus status,
            List<String> categories
    ) {
        this.auctionId = auctionId;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.sellerUserName = sellerUserName;
        this.startPrice = startPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.categories = categories;
    }

}
