package com.auction.service;

import com.auction.entity.Auction;
import com.auction.repository.AuctionRepository;
import com.common.dto.bid.BidPlacedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EventListenerService {

    private final AuctionRepository auctionRepository;

    @KafkaListener(topics = "bid-placed-topic", groupId = "auction-group")
    public void handleBidPlaced(BidPlacedEvent event) {
        Auction auction = auctionRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new EntityNotFoundException("경매를 찾을 수 없습니다."));

        if (event.getPrice() > auction.getCurrentPrice()) {
            auction.updateHighestBid(event.getUserId(), event.getPrice());
            auctionRepository.save(auction);
        }
    }

}
