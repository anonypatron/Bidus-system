package com.auction.service;

import com.auction.entity.Auction;
import com.common.dto.auction.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventPublishService {

    private final KafkaTemplate<String, AuctionCreatedEvent> startedKafkaTemplate;
    private final KafkaTemplate<String, AuctionSyncEvent> auctionSyncKafkaTemplate;
    private final KafkaTemplate<String, AuctionDeletedEvent> auctionDeletedKafkaTemplate;
    private final KafkaTemplate<String, AuctionStartedEvent> auctionStartedKafkaTemplate;
    private final KafkaTemplate<String, AuctionClosedEvent> auctionClosedKafkaTemplate;
    private final KafkaTemplate<String, AuctionCanceledEvent> auctionCanceledKafkaTemplate;

    // created auction
    public void publishAuctionCreatedEvent(Auction auction) {
        List<String> categories = auction.getAuctionCategories().stream()
                .map(auctionCategory -> auctionCategory.getCategory().getName())
                .collect(Collectors.toList());

        AuctionCreatedEvent event = AuctionCreatedEvent.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .imagePath(auction.getImagePath())
                .sellerUserName(auction.getSellerUserName())
                .startPrice(auction.getStartPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .categories(categories)
                .build();
        startedKafkaTemplate.send("auction-create-topic", event);
    }

    // update auction
    public void publishAuctionSyncEvent(Auction auction, List<String> categories) {
        AuctionSyncEvent event = AuctionSyncEvent.builder()
                .id(auction.getId())
                .sellerId(auction.getSellerId())
                .sellerUserName(auction.getSellerUserName())
                .imagePath(auction.getImagePath())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .categories(categories)
                .bookmarkCount((long) auction.getAuctionBookmarks().size())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
        auctionSyncKafkaTemplate.send("auction-sync-topic", event);
    }

    // delete auction
    public void publishAuctionDelete(Auction auction) {
        AuctionDeletedEvent event = AuctionDeletedEvent.builder()
                .auctionId(auction.getId())
                .build();
        auctionDeletedKafkaTemplate.send("auction-delete-topic", event);
    }

    // create auction
    public void publishAuctionStartedEvent(Auction auction) {
        AuctionStartedEvent event = AuctionStartedEvent.builder()
                .auctionId(auction.getId())
                .status(auction.getStatus())
                .build();
        auctionStartedKafkaTemplate.send("auction-start-topic", event);
    }

    // closed auction
    public void publishAuctionClosedEvent(Auction auction) {
        AuctionClosedEvent event = AuctionClosedEvent.builder()
                .auctionId(auction.getId())
                .winnerId(auction.getWinnerId()) // null일 수도 있음 (유찰)
                .finalPrice(auction.getFinalPrice())
                .status(auction.getStatus())
                .build();
        log.info("{}번 가격 : {}, 상태 : {} 메시지 전송 완료", auction.getId(), auction.getFinalPrice(), auction.getStatus());
        auctionClosedKafkaTemplate.send("auction-close-topic", event);
    }

    // cancel auction
    public void publishAuctionCanceledEvent(Auction auction) {
        AuctionCanceledEvent event = AuctionCanceledEvent.builder()
                .auctionId(auction.getId())
                .status(auction.getStatus())
                .build();
        auctionCanceledKafkaTemplate.send("auction-cancel-topic", event);
    }

}
