package com.bidding.service;

import com.common.dto.bid.BidFailedEvent;
import com.common.dto.bid.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventPublishService {

    private final KafkaTemplate<String, BidPlacedEvent> kafkaBidPlacedTemplate;
    private final KafkaTemplate<String, BidFailedEvent> kafkaBidFailedKafkaTemplate;

    public void publishBidSuccessEvent(BidPlacedEvent event) {
        log.info("{}으로 메시지 발행", "bid-placed-topic");
        kafkaBidPlacedTemplate.send("bid-placed-topic", event);
    }

    public void publishBidFailedEvent(BidFailedEvent event) {
        log.info("{}으로 메시지 발행", "bid-failed-topic");
        kafkaBidFailedKafkaTemplate.send("bid-failed-topic", event);
    }

}
