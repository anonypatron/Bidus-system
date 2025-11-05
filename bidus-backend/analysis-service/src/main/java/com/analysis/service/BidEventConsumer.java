package com.analysis.service;

import com.analysis.entity.BidHistory;
import com.analysis.repository.BidHistoryRepository;
import com.common.dto.bid.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BidEventConsumer {

    private final BidHistoryRepository bidHistoryRepository;

    @KafkaListener(topics = "bid-placed-topic", groupId = "analysis-group")
    public void handleBidPlacedEvent(BidPlacedEvent bidPlacedEvent) {
        log.info("{} 메시지 수신", "bid-placed-topic");
        BidHistory bidHistory = BidHistory.builder()
                .auctionId(bidPlacedEvent.getAuctionId())
                .userId(bidPlacedEvent.getUserId())
                .price(bidPlacedEvent.getPrice())
                .bidTime(bidPlacedEvent.getBidTime())
                .build();
        bidHistoryRepository.save(bidHistory);
    }

}
