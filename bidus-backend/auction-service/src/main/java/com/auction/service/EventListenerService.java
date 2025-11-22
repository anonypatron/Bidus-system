package com.auction.service;

import com.auction.entity.Auction;
import com.auction.repository.AuctionRepository;
import com.common.dto.bid.BidPlacedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventListenerService {

    private final AuctionRepository auctionRepository;

    @Transactional
    @KafkaListener(topics = "bid-placed-topic", groupId = "auction-group")
    public void handleBidPlaced(BidPlacedEvent event) {
        Auction auction = auctionRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("경매를 찾을 수 없습니다."));

        if (event.getPrice() > auction.getCurrentPrice()) {
            auction.updateHighestBid(event.getUserId(), event.getPrice());
            log.info("auctionId : {}, userId: {}, highestBiddingId: {}", auction.getId(), event.getUserId(), auction.getHighestBidderId());
        }
    }

}
