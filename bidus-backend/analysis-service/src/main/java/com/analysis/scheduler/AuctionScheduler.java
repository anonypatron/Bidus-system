package com.analysis.scheduler;

import com.analysis.dto.GraphPointDto;
import com.analysis.entity.AuctionHistory;
import com.analysis.entity.BidHistory;
import com.analysis.repository.AuctionHistoryRepository;
import com.analysis.repository.BidHistoryRepository;
import com.common.AuctionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuctionScheduler {

    private final AuctionHistoryRepository auctionHistoryRepository;
    private final BidHistoryRepository bidHistoryRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void generateGraphData() {
        // 종료된 경매
        List<AuctionHistory> endedAuctionHistories = auctionHistoryRepository.findEndedAuctionsWithoutGraphData(Instant.now());
        log.info("{}개의 종료된 경매", endedAuctionHistories.size());

        for (AuctionHistory auctionHistory : endedAuctionHistories) {
            if (auctionHistory.getStatus() != AuctionStatus.CLOSED) {
                log.error("{}번 경매 상태 : {}", auctionHistory.getAuctionId(), auctionHistory.getStatus());
                continue;
            }

            log.info("{}번 경매 그래프 생성 시작", auctionHistory.getAuctionId());
            List<BidHistory> bidHistories = bidHistoryRepository.findAllByAuctionIdOrderByBidTimeAsc(auctionHistory.getId());

            List<GraphPointDto> graphData = bidHistories.stream()
                    .map(GraphPointDto::fromBidHistory)
                    .collect(Collectors.toList());

            auctionHistory.setBidHistoryGraph(graphData);
        }
    }

}
