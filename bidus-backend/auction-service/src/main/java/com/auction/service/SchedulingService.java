package com.auction.service;

import com.auction.entity.Auction;
import com.auction.repository.AuctionRepository;
import com.common.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SchedulingService {

    private final AuctionRepository auctionRepository;
    private final EventPublishService eventPublishService;

    // scheduler에 의해 5분마다 실행 -> 시작된 경매가 있는지 확인 -> 상태변경 및 이벤트 알림 및 저장
    @Transactional
    public void startScheduledAuctions() {
        log.info("경매 시작 스케줄러. 서버 현재 시간(UTC): {}", Instant.now());

        // 카테고리까지 한 번의 쿼리로 가져옴
        List<Auction> scheduledAuctions = auctionRepository.findAllByStatusAndStartTimeBeforeWithCategories(
                AuctionStatus.SCHEDULED, Instant.now()
        );

        for (Auction auction : scheduledAuctions) {
            auction.changeStatus(AuctionStatus.IN_PROGRESS);
            eventPublishService.publishAuctionStartedEvent(auction);
            log.info("start scheduled auctions: {}", auction.getTitle());
        }

        auctionRepository.saveAll(scheduledAuctions);
    }

    // scheduler에 의해 5분마다 실행 -> 종료된 경매가 있는지 확인 -> 상태변경 및 이벤트 알림 및 저장
    @Transactional
    public void closeFinishedAuctions() {
        log.info("경매 종료 스케줄러. 서버 현재 시간(UTC): {}", Instant.now());
        List<Auction> finishedAuctions = auctionRepository.findAllByStatusAndEndTimeBefore(
                AuctionStatus.IN_PROGRESS, Instant.now()
        );

        for (Auction auction : finishedAuctions) {
            auction.closeAuction();
            eventPublishService.publishAuctionClosedEvent(auction);
            log.info("end scheduled auctions: {}", auction.getTitle());
        }

        auctionRepository.saveAll(finishedAuctions);
    }

}
