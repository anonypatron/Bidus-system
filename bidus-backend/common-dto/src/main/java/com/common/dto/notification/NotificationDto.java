package com.common.dto.notification;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotificationDto {

    private Long auctionId;
    private String message;

    @Builder
    public NotificationDto(Long auctionId, String message) {
        this.auctionId = auctionId;
        this.message = message;
    }

}
