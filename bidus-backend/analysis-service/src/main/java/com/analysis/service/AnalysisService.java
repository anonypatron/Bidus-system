package com.analysis.service;

import com.analysis.dto.AuctionHistoryResponseDto;
import com.analysis.entity.AuctionHistory;
import com.analysis.repository.AuctionHistoryRepository;
import com.common.AuctionStatus;
import com.common.error.code.ErrorCode;
import com.common.exception.auction.AuctionNotFoundException;
import com.common.exception.auction.AuctionStatusException;
import com.common.exception.graph.GraphNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AnalysisService {

    private final AuctionHistoryRepository auctionHistoryRepository;

    @Transactional(readOnly = true)
    public AuctionHistoryResponseDto getAuctionAnalysis(Long auctionId) {
        AuctionHistory auctionHistory = auctionHistoryRepository.findByAuctionId(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(ErrorCode.AUCTION_NOT_FOUND));

        if (auctionHistory.getBidHistoryGraph() == null || auctionHistory.getBidHistoryGraph().isEmpty()) {
            log.error("Graph is null -> 경매 번호 : {}", auctionHistory.getId());
            throw new GraphNotFoundException(ErrorCode.Graph_NOT_FOUND);
        }

        if (AuctionStatus.CLOSED != auctionHistory.getStatus()) {
            log.error("Auction is not closed -> auctionId : {}", auctionHistory.getId());
            throw new AuctionStatusException(ErrorCode.INVALID_AUCTION_STATUS);
        }

        return AuctionHistoryResponseDto.fromEntity(auctionHistory);
    }

    @Transactional(readOnly = true)
    public List<AuctionHistoryResponseDto> getAuctionAnalyzes(List<Long> auctionIds) {
        log.info("id size : {}", auctionIds.size());

        List<AuctionHistory> auctionHistories = auctionHistoryRepository.findByAuctionIdIn(auctionIds);

        for (AuctionHistory auctionHistory : auctionHistories) {
            if (auctionHistory.getBidHistoryGraph() == null || auctionHistory.getBidHistoryGraph().isEmpty()) {
                log.error("Graph is null -> 경매 번호 : {}", auctionHistory.getId());
                throw new GraphNotFoundException(ErrorCode.Graph_NOT_FOUND);
            }
            log.info("auctionStatus : {}", auctionHistory.getStatus());
        }

        return auctionHistories.stream()
                .map(AuctionHistoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

}
