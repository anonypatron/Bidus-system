package com.auction.dto.response;

import com.common.AuctionStatus;
import com.auction.entity.Auction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
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

    private List<String> categories;

    private Long winnerId; // 낙찰자 id
    private Long finalPrice; // 낙찰가

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    @Builder
    public AuctionResponseDto(
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
            AuctionStatus status,
            Long winnerId,
            Long finalPrice,
            boolean isBookmarked
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
        this.winnerId = winnerId;
        this.finalPrice = finalPrice;
        this.isBookmarked = isBookmarked;
    }

    public static AuctionResponseDto fromEntity(Auction auction, boolean isBookmarked) {
        List<String> categoryNames = auction.getAuctionCategories().stream()
                .map(auctionCategory -> auctionCategory.getCategory().getName())
                .collect(Collectors.toList());

        return AuctionResponseDto.builder()
                .id(auction.getId())
                .sellerId(auction.getSellerId())
                .sellerUserName(auction.getSellerUserName())
                .imagePath(auction.getImagePath())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .categories(categoryNames)
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .winnerId(auction.getWinnerId())
                .finalPrice(auction.getFinalPrice())
                .isBookmarked(isBookmarked)
                .build();
    }

}
