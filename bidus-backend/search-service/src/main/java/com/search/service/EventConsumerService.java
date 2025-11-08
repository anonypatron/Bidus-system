package com.search.service;

import com.common.dto.auction.*;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import com.search.entity.AuctionSearchDocument;
import com.search.repository.AuctionSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventConsumerService {

    private final AuctionSearchRepository auctionSearchRepository;

    // *************************************************************
    // elasticsearch는 @Transactional 적용이 안됨. 명시적으로 save 해줄 것
    @KafkaListener(topics = "auction-create-topic", groupId = "search-group")
    public void handleAuctionCreatedEvent(AuctionCreatedEvent event) {
        log.info("Kafka Message Received: {}", event.getAuctionId());

        AuctionSearchDocument document = AuctionSearchDocument.builder()
                .id(event.getAuctionId())
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

    @KafkaListener(topics = "auction-sync-topic", groupId = "search-group")
    public void handleAuctionSyncEvent(AuctionSyncEvent event) {
        log.info("Kafka Message Received: {}", event.getId());
        AuctionSearchDocument document = auctionSearchRepository.findById(event.getId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        document.setSellerUserName(event.getSellerUserName());
        document.setTitle(event.getTitle());
        document.setDescription(event.getDescription());
        document.setImagePath(event.getImagePath());
        document.setCategories(event.getCategories());
        document.setStartPrice(event.getStartPrice());
        document.setCurrentPrice(event.getCurrentPrice());
        document.setStartTime(event.getStartTime());
        document.setEndTime(event.getEndTime());
        document.setStatus(event.getStatus());

        auctionSearchRepository.save(document);
    }

    // delete auction
    @KafkaListener(topics = "auction-delete-topic", groupId = "search-group")
    public void handleAuctionDeleteEvent(AuctionDeletedEvent event) {
        auctionSearchRepository.deleteById(event.getAuctionId());
    }

    // update status
    @KafkaListener(topics = "auction-start-topic", groupId = "search-group")
    public void handleAuctionStartEvent(AuctionStartedEvent event) {
        AuctionSearchDocument document = auctionSearchRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        document.setStatus(event.getStatus());
        auctionSearchRepository.save(document);
    }

    @KafkaListener(topics = "auction-close-topic", groupId = "search-group")
    public void handleAuctionClosedEvent(AuctionClosedEvent event) {
        AuctionSearchDocument document = auctionSearchRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        document.setStatus(event.getStatus());
        auctionSearchRepository.save(document);
    }

    @KafkaListener(topics = "auction-cancel-topic", groupId = "search-group")
    public void handleAuctionCanceledEvent(AuctionCanceledEvent event) {
        AuctionSearchDocument document = auctionSearchRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        document.setStatus(event.getStatus());
        auctionSearchRepository.save(document);
    }

}
