package com.web.bff.controller;

import com.common.dto.user.UserPrincipal;
import com.web.bff.service.NotificationBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationBffController {

    private final NotificationBffService notificationBffService;

    @GetMapping(value = "/{auctionId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> subscribe(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        Long userId = userPrincipal.getId();
        return notificationBffService.subscribe(userId, auctionId);
    }

}
