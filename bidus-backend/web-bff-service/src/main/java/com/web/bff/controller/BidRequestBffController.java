package com.web.bff.controller;

import com.common.dto.user.UserPrincipal;
import com.web.bff.dto.bidrequest.BidRequestDto;
import com.web.bff.service.BidRequestBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bids")
public class BidRequestBffController {

    private final BidRequestBffService bidRequestBffService;

    @PostMapping
    public Mono<ResponseEntity<Void>> placeBid(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody BidRequestDto dto
    ) {
       return bidRequestBffService.placeBid(principal.getId(), dto)
               .then(Mono.just(ResponseEntity.ok().build()));
    }

}
