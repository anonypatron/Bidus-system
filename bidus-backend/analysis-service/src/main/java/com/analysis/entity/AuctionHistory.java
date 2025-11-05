package com.analysis.entity;

import com.analysis.dto.GraphPointDto;
import com.common.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "auction_history")
public class AuctionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Long auctionId;
    private Long startPrice;
    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    private Instant startTime;
    private Instant endTime;

    // 그래프 데이터를 저장
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<GraphPointDto> bidHistoryGraph;

    @Builder
    public AuctionHistory(
            String title,
            Long auctionId,
            Long startPrice,
            Long finalPrice,
            AuctionStatus status,
            Instant startTime,
            Instant endTime
    ) {
        this.title = title;
        this.startPrice = startPrice;
        this.finalPrice = finalPrice;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.auctionId = auctionId;
    }

}
