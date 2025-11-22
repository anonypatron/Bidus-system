package com.auction.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentBiddingCount {
    private Long count;
}
