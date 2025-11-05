package com.search.entity;

import com.common.AuctionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Document(indexName = "auctions_v1")
public class AuctionSearchDocument {

    @Id
    private Long id; // auctionId

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String description;

    @Field(type = FieldType.Keyword, index = false)
    private String imagePath;

    @Field(type = FieldType.Text)
    private String sellerUserName;

    @Field(type = FieldType.Keyword)
    private AuctionStatus status;

    @Field(type = FieldType.Long)
    private Long startPrice;

    @Field(type = FieldType.Long)
    private Long currentPrice;

    @Field(type = FieldType.Date)
    private Instant startTime;

    @Field(type = FieldType.Date)
    private Instant endTime;

    @Field(type = FieldType.Keyword)
    private List<String> categories;

    @Builder
    public AuctionSearchDocument(
            Long id,
            String title,
            String description,
            String imagePath,
            String sellerUserName,
            AuctionStatus status,
            Long startPrice,
            Long currentPrice,
            Instant startTime,
            Instant endTime,
            List<String> categories
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.sellerUserName = sellerUserName;
        this.status = status;
        this.startPrice = startPrice;
        this.currentPrice = currentPrice;
        this.startTime = startTime;
        this.endTime = endTime;
        this.categories = categories;
    }

}
