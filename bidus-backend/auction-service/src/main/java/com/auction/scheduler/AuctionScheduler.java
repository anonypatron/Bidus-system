package com.auction.scheduler;

import com.auction.service.AuctionService;
import com.auction.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuctionScheduler {

    private final SchedulingService schedulingService;

    // cron = 초 분 시 일 월 요일
    @Scheduled(cron = "0 * * * * *")
    public void manageAuctionStatus() {
        log.info("경매 상태 업데이트 시작. 서버 현재 시간(UTC 추정): {}", Instant.now());
        schedulingService.startScheduledAuctions();
        schedulingService.closeFinishedAuctions();
    }

}
