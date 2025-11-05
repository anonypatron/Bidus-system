package com.analysis.service;

import com.analysis.entity.AuctionHistory;
import com.analysis.repository.AuctionHistoryRepository;
import com.common.dto.auction.AuctionClosedEvent;
import com.common.dto.auction.AuctionStartedEvent;
import com.common.dto.auction.AuctionSyncEvent;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuctionEventConsumer {

    private final AuctionHistoryRepository auctionHistoryRepository;

    @Transactional
    @KafkaListener(topics = "auction-sync-topic", groupId = "analysis-group")
    public void handleAuctionSyncEvent(AuctionSyncEvent event) {
        log.info("{} 메시지 수신", "auction-sync-topic");

        AuctionHistory history = auctionHistoryRepository.findByAuctionId(event.getId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        history.setTitle(event.getTitle());
        history.setStatus(event.getStatus());
        history.setStartTime(event.getStartTime());
        history.setEndTime(event.getEndTime());
    }

    @KafkaListener(topics = "auction-started-topic", groupId = "analysis-group")
    public void handleAuctionStarted(AuctionStartedEvent event) {
        log.info("{} 메시지 수신", "auction-started-topic");

        AuctionHistory auctionHistory = AuctionHistory.builder()
                .title(event.getTitle())
                .auctionId(event.getAuctionId())
                .startPrice(event.getStartPrice())
                .status(event.getStatus())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .build();
        auctionHistoryRepository.save(auctionHistory);
    }

    @Transactional
    @KafkaListener(topics = "auction-closed-topic", groupId = "analysis-group")
    public void handleAuctionClosed(AuctionClosedEvent event) {
        log.info("{}번 가격 : {}, 상태 : {} 수신", event.getAuctionId(), event.getFinalPrice(), event.getStatus());

        AuctionHistory auctionHistory = auctionHistoryRepository.findByAuctionId(event.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        auctionHistory.setStatus(event.getStatus());
        auctionHistory.setFinalPrice(event.getFinalPrice());
        auctionHistoryRepository.save(auctionHistory);
    }

}
