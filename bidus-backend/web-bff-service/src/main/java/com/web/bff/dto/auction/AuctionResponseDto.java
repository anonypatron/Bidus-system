package com.web.bff.dto.auction;

import com.common.AuctionStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class AuctionResponseDto {

    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private String sellerUserName;
    private Long sellerId;

    private AuctionStatus status;

    private Long startPrice;
    private Long currentPrice;

    private Instant startTime;
    private Instant endTime;

    private List<String>categories;

    private Long winnerId;
    private Long finalPrice;

    private boolean bookmarked;

}
