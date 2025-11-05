package com.auction.dto.request;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class AuctionUpdateRequestDto {

    private String title;
    private String description;
    private List<String> categories;
    private Long startPrice;

    private Instant startTime;
    private Instant endTime;

}
