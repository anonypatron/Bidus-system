package com.bidding.entity;

import com.common.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
public class AuctionBidInfo {

    // auction-service에서 만든 데이터를 보관하기 위함.(직접 데이터를 생산하는 게 아님, replica용)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    private Instant endTime;

    // 낙관적 락을 위한 버전
    @Version
    private Long version;

    @Builder
    public AuctionBidInfo(Long id, Long currentPrice, AuctionStatus status, Instant endTime) {
        this.id = id;
        this.currentPrice = currentPrice;
        this.status = status;
        this.endTime = endTime;
    }

    public void updatePrice(Long newPrice) {
        this.currentPrice = newPrice;
    }

    public void changeStatus(AuctionStatus newStatus) {
        this.status = newStatus;
    }

}
