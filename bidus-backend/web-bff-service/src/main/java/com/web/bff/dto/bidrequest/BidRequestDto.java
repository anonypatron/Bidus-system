package com.web.bff.dto.bidrequest;

public record BidRequestDto(
        Long auctionId,
        Long price
) {
}
