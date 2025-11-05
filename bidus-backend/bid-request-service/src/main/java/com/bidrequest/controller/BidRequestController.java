package com.bidrequest.controller;

import com.bidrequest.dto.request.BidRequestDto;
import com.bidrequest.service.BidRequestService;
import com.common.dto.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bids")
public class BidRequestController {

    private final BidRequestService bidRequestService;

    // 경매 입찰 요청 -> http://localhost/api/bids
    @PostMapping
    public ResponseEntity<Void> placeBid(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody BidRequestDto dto
    ) {
        Long userId = principal.getId();
        bidRequestService.bidPlace(userId, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
