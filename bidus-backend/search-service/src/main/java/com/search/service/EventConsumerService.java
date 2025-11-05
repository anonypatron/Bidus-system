package com.search.service;

import com.common.dto.auction.AuctionDeleteEvent;
import com.common.dto.auction.AuctionStartedEvent;
import com.common.dto.auction.AuctionSyncEvent;
import com.search.entity.AuctionSearchDocument;
import com.search.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventConsumerService {

    private final AuctionSearchRepository auctionSearchRepository;

    @Transactional
    @KafkaListener(topics = "auction-sync-topic", groupId = "search-group")
    public void handleAuctionSyncEvent(AuctionSyncEvent event) {
        log.info("Kafka Message Received: {}", event.getId());

        AuctionSearchDocument auctionSearchDocument = AuctionSearchDocument.builder()
                .id(event.getId())
                .sellerUserName(event.getSellerUserName())
                .title(event.getTitle())
                .description(event.getDescription())
                .imagePath(event.getImagePath())
                .categories(event.getCategories())
                .startPrice(event.getStartPrice())
                .currentPrice(event.getCurrentPrice())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .status(event.getStatus())
                .build();
        auctionSearchRepository.save(auctionSearchDocument);
    }

    @KafkaListener(topics = "auction-started-topic", groupId = "search-group")
    public void handleAuctionStartedEvent(AuctionStartedEvent event) {
        log.info("Kafka Message Received: {}", event.getAuctionId());

        AuctionSearchDocument document = AuctionSearchDocument.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .imagePath(event.getImagePath())
                .sellerUserName(event.getSellerUserName())
                .status(event.getStatus())
                .startPrice(event.getStartPrice())
                .currentPrice(event.getStartPrice())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .categories(event.getCategories())
                .build();
        auctionSearchRepository.save(document);
    }

    @Transactional
    @KafkaListener(topics = "auction-delete-topic", groupId = "search-group")
    public void handleAuctionDeleteEvent(AuctionDeleteEvent event) {
        auctionSearchRepository.deleteById(event.getAuctionId());
    }

}
