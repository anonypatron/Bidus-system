package com.auction.service;

import com.auction.entity.Auction;
import com.common.dto.auction.AuctionClosedEvent;
import com.common.dto.auction.AuctionDeleteEvent;
import com.common.dto.auction.AuctionStartedEvent;
import com.common.dto.auction.AuctionSyncEvent;
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

    private final KafkaTemplate<String, AuctionStartedEvent> startedKafkaTemplate;
    private final KafkaTemplate<String, AuctionClosedEvent> closedKafkaTemplate;
    private final KafkaTemplate<String, AuctionSyncEvent> auctionSyncKafkaTemplate;
    private final KafkaTemplate<String, AuctionDeleteEvent> auctionDeleteKafkaTemplate;

    // update auction
    public void publishAuctionSyncEvent(Auction auction, List<String> categories) {
        AuctionSyncEvent auctionSyncEvent = AuctionSyncEvent.builder()
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
        auctionSyncKafkaTemplate.send("auction-sync-topic", auctionSyncEvent);
    }

    // delete auction
    public void publishAuctionDelete(Auction auction) {
        AuctionDeleteEvent auctionDeleteEvent = AuctionDeleteEvent.builder()
                .auctionId(auction.getId())
                .build();
        auctionDeleteKafkaTemplate.send("auction-delete-topic", auctionDeleteEvent);
    }

    // started auction
    public void publishAuctionStartedEvent(Auction auction) {
        List<String> categories = auction.getAuctionCategories().stream()
                .map(auctionCategory -> auctionCategory.getCategory().getName())
                .collect(Collectors.toList());

        AuctionStartedEvent event = AuctionStartedEvent.builder()
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
        startedKafkaTemplate.send("auction-started-topic", event);
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
        closedKafkaTemplate.send("auction-closed-topic", event);
    }

}
