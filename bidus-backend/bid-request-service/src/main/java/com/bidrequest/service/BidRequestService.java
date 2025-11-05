package com.bidrequest.service;

import com.bidrequest.dto.request.BidRequestDto;
import com.common.dto.bid.BidRequestEvent;
import com.common.error.code.ErrorCode;
import com.common.exception.value.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class BidRequestService {

    private final KafkaTemplate<String, BidRequestEvent> kafkaTemplate;

    public void bidPlace(Long userId, BidRequestDto dto) {
        if (userId == null || dto.getAuctionId() == null || dto.getAuctionId() == 0L) {
            throw new InvalidRequestException(ErrorCode.INVALID_REQUEST);
        }

        BidRequestEvent bidRequestEvent = BidRequestEvent.builder()
                .userId(userId)
                .price(dto.getPrice())
                .auctionId(dto.getAuctionId())
                .build();
        kafkaTemplate.send("bid-request-topic", bidRequestEvent);
        log.info("{}으로 메시지 발행 : 경매 Id: {}", "bid-request-topic", bidRequestEvent.getAuctionId());
    }

}
