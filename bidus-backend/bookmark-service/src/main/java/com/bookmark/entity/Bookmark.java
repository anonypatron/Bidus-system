package com.bookmark.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long auctionId;

    @Builder
    public Bookmark(Long userId, Long auctionId) {
        this.userId = userId;
        this.auctionId = auctionId;
    }

}
