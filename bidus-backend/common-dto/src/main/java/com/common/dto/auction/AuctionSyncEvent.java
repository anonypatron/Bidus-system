package com.common.dto.auction;

import com.common.AuctionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor
@Getter
public class AuctionSyncEvent {

    private Long id;
    private Long sellerId;

    private String sellerUserName;
    private String imagePath;
    private String title;
    private String description;

    private List<String> categories;

    private Long startPrice;
    private Long currentPrice;

    private Instant startTime;
    private Instant endTime;

    private AuctionStatus status;

    @Builder
    public AuctionSyncEvent (
            Long id,
            Long sellerId,
            String sellerUserName,
            String imagePath,
            String title,
            String description,
            List<String> categories,
            Long startPrice,
            Long currentPrice,
            Instant startTime,
            Instant endTime,
            AuctionStatus status
    ) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerUserName = sellerUserName;
        this.imagePath = imagePath;
        this.title = title;
        this.description = description;
        this.categories = categories;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

}
