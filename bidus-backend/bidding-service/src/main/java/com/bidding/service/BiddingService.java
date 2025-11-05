package com.bidding.service;

import com.bidding.entity.AuctionBidInfo;
import com.bidding.entity.Bid;
import com.bidding.repository.AuctionBidInfoRepository;
import com.bidding.repository.BidRepository;
import com.common.AuctionStatus;
import com.common.dto.bid.BidFailedEvent;
import com.common.dto.bid.BidPlacedEvent;
import com.common.dto.bid.BidRequestEvent;
import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;
import com.common.exception.auction.AuctionNotFoundException;
import com.common.exception.auction.AuctionStatusException;
import com.common.exception.value.InvalidInputPriceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Slf4j
public class BiddingService {

    private final AuctionBidInfoRepository auctionBidInfoRepository;
    private final EventPublishService eventPublishService;
    private final BidRepository bidRepository;

    // 입찰시도(하나의 트랜잭션으로 하고 version을 통해 낙관적 락을 구현.
    // 예를들어 a라는 사용자와 b라는 사용자가 동시에 1000원을 입찰
    // a의 입찰이 받아들여지고 version이 바뀜. 이 때 b의 입찰을 받아들이고 update할 때
    // version이 달라지면 업데이트 실패 -> 트랜잭션 복구
    // 따라서 하나의 요청만 받아들여지게 됨.
    @KafkaListener(topics = "bid-request-topic", groupId = "bidding-group")
    @Transactional
    public void placeBid(BidRequestEvent event) {
        log.info("{} 메시지 수신", "bid-request-topic");
        AuctionBidInfo auctionBidInfo = auctionBidInfoRepository.findById(event.getAuctionId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        try {
            validate(event, auctionBidInfo);
        } catch (BusinessException e) {
            log.error("Error Code : {}, Error Status : {}, Error Message: {}",
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getStatus(),
                    e.getErrorCode().getMessage());

            BidFailedEvent failedEvent = BidFailedEvent.builder()
                    .auctionId(event.getAuctionId())
                    .userId(event.getUserId())
                    .reason(e.getMessage())
                    .build();
            eventPublishService.publishBidFailedEvent(failedEvent);
        }

        auctionBidInfo.updatePrice(event.getPrice());

        Bid newBid = Bid.builder()
                .userId(event.getUserId())
                .auctionId(event.getAuctionId())
                .price(event.getPrice())
                .build();
        bidRepository.save(newBid);

        BidPlacedEvent bidPlacedEvent = BidPlacedEvent.builder()
                .userId(newBid.getUserId())
                .auctionId(newBid.getAuctionId())
                .price(newBid.getPrice())
                .bidTime(newBid.getBidTime())
                .build();
        eventPublishService.publishBidSuccessEvent(bidPlacedEvent);
    }

    private void validate(BidRequestEvent event, AuctionBidInfo auctionBidInfo) {
        if (Instant.now().isAfter(auctionBidInfo.getEndTime())) {
            throw new AuctionStatusException(ErrorCode.AUCTION_FINISHED);
        }
        if (!AuctionStatus.IN_PROGRESS.equals(auctionBidInfo.getStatus())) {
            throw new AuctionStatusException(ErrorCode.INVALID_AUCTION_STATUS);
        }
        if (auctionBidInfo.getCurrentPrice() >= event.getPrice()) {
            throw new InvalidInputPriceException(ErrorCode.INVALID_INPUT_PRICE);
        }
    }

}
