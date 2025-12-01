package com.notification.service;

import com.common.dto.bid.BidFailedEvent;
import com.common.dto.bid.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventListenerService {

    private final NotificationService notificationService;

    @KafkaListener(topics = "bid-placed-topic", groupId = "notification-group")
    public void handleBidPlaced(BidPlacedEvent event) {
        log.info("BidPlacedEvent received: {}", event.getPrice());
        notificationService.sendPriceUpdateToAll(event.getAuctionId(), event.getPrice());
    }

    @KafkaListener(topics = "bid-failed-topic", groupId = "notification-group")
    public void handleBidFailed(BidFailedEvent event) {
        log.info("BidFailedEvent received: {}", event.getReason());
        notificationService.sendNotificationToUser(
                event.getUserId(),
                event.getAuctionId(),
                "bid-failed",
                event.getReason()
        );
    }

}
