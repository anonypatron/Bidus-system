package com.analysis.dto;

import com.analysis.entity.BidHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class GraphPointDto {

    private Instant time;
    private Long price;
    private Long userId;

    @Builder
    public GraphPointDto(Instant time, Long price, Long userId) {
        this.time = time;
        this.price = price;
        this.userId = userId;
    }

    public static GraphPointDto fromBidHistory(BidHistory bidHistory) {
        return GraphPointDto.builder()
                .time(bidHistory.getBidTime())
                .price(bidHistory.getPrice())
                .userId(bidHistory.getUserId())
                .build();
    }

}
