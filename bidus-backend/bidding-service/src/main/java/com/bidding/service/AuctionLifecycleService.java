package com.bidding.service;

import com.bidding.entity.AuctionBidInfo;
import com.bidding.repository.AuctionBidInfoRepository;
import com.common.AuctionStatus;
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
public class AuctionLifecycleService {

    private final AuctionBidInfoRepository auctionBidInfoRepository;

    @Transactional
    @KafkaListener(topics = "auction-sync-topic", groupId = "bidding-group")
    public void handleAuctionSyncEvent(AuctionSyncEvent event) {
        AuctionBidInfo auctionBidInfo = auctionBidInfoRepository.findById(event.getId())
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));
        auctionBidInfo.setStatus(event.getStatus());
        auctionBidInfo.setEndTime(event.getEndTime());
    }

    // 경매 시작 알림을 받고 경매정보를 bidding-service에 복사하여 저장
    // 앞으로는 auction-service의 데이터를 읽지 않고도 바로바로 접근이 가능함
    @KafkaListener(topics = "auction-started-topic", groupId = "bidding-group")
    public void handleAuctionStarted(AuctionStartedEvent event) {
        if (auctionBidInfoRepository.existsById(event.getAuctionId())) {
            log.info("이미 시작된 경매입니다.");
            return;
        }

        AuctionBidInfo auctionBidInfo = AuctionBidInfo.builder()
                .id(event.getAuctionId())
                .status(event.getStatus())
                .endTime(event.getEndTime())
                .currentPrice(event.getStartPrice())
                .build();
        auctionBidInfoRepository.save(auctionBidInfo);
    }

    // 경매 종료 알림을 받고 상태업데이트
    @Transactional
    @KafkaListener(topics = "auction-closed-topic", groupId = "bidding-group")
    public void handleAuctionClosed(AuctionClosedEvent event) {
        log.info("경매 종료 이벤트: auctionId={}", event.getAuctionId());

        auctionBidInfoRepository.findById(event.getAuctionId()).ifPresent(auctionBidInfo -> {
            if (AuctionStatus.CLOSED.equals(auctionBidInfo.getStatus())) {
                return;
            }
            auctionBidInfo.changeStatus(AuctionStatus.CLOSED);
            auctionBidInfoRepository.save(auctionBidInfo);
        });
    }

}
