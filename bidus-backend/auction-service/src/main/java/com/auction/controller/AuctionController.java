package com.auction.controller;

import com.auction.dto.request.AuctionCreateRequestDto;
import com.auction.dto.request.AuctionUpdateRequestDto;
import com.auction.dto.response.AuctionPriceDto;
import com.auction.dto.response.AuctionResponseDto;
import com.auction.service.AuctionService;
import com.common.AuctionStatus;
import com.common.dto.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 생성
    // POST http://localhost/api/auctions
    @PostMapping
    public ResponseEntity<AuctionResponseDto> createAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("auctionData") AuctionCreateRequestDto dto,
            @RequestPart("image") MultipartFile image
    ) {
        AuctionResponseDto res = auctionService.save(
                userPrincipal.getId(),
                dto,
                image
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 특정 경매 검색
    // GET http://localhost/api/auctions/1
    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponseDto> getAuction(
            @PathVariable Long auctionId
    ) {
        AuctionResponseDto res = auctionService.getAuction(auctionId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 특정 경매들을 검색
    // GET http://localhost/api/auctions
    @GetMapping("/list")
    public ResponseEntity<Page<AuctionResponseDto>> getAuctionsByIds(
            @RequestParam(value = "ids") Set<Long> ids,
            @RequestParam(defaultValue = "IN_PROGRESS") AuctionStatus status,
            @PageableDefault(page = 0, size = 9) Pageable pageable
    ) {
        Page<AuctionResponseDto> auctions = auctionService.getAuctionsByIds(ids, status, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
    }

    // 상태에 따른 페이지 객체
    // GET http://localhost/api/auctions?status=CLOSED&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<AuctionResponseDto>> getAuctions(
            @RequestParam(defaultValue = "IN_PROGRESS") AuctionStatus status,
            @PageableDefault(page = 0, size = 9) Pageable pageable
    ) {
        Page<AuctionResponseDto> res = auctionService.getAuctions(status, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 사용자의 경매(구매) 기록 조회 GET http://localhost/api/auctions/history?role=seller&staus=IN_PROGRESS
    // 사용자의 경매(판매) 기록 조회 GET http://localhost/api/auctions/history
    @GetMapping("/history")
    public ResponseEntity<List<AuctionResponseDto>> getHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "role", defaultValue = "seller") String role,
            @RequestParam(value = "status", defaultValue = "CLOSED") AuctionStatus status
    ) {
        List<AuctionResponseDto> history = auctionService.getAuctionHistory(
                userPrincipal.getId(),
                role,
                status
        );
        return ResponseEntity.status(HttpStatus.OK).body(history);
    }

    // 현재 진행중이고 본인이 입찰한(가장 높은 금액) 경매 리스트 리턴
    @GetMapping("/current-bidding")
    public ResponseEntity<List<AuctionResponseDto>> getCurrentBidding(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        List<AuctionResponseDto> auctions = auctionService.getAuctionCurrentBidding(userId);
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
    }

    // 준비중일 때만 수정 가능
    // PATCH http://localhost/api/auctions/1
    @PatchMapping("/{auctionId}")
    public ResponseEntity<Void> updateAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId,
            @RequestPart("auctionData") AuctionUpdateRequestDto dto,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        auctionService.update(userPrincipal.getId(), auctionId, dto, image);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 준비중일 때만 삭제 가능
    // DELETE http://localhost/api/auctions/1
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> deleteAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        auctionService.delete(userPrincipal.getId(), auctionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // *** 테스트용 api endpoint임 실제로 사용하지 않음 ***
    @GetMapping("/{id}/current-price")
    public ResponseEntity<AuctionPriceDto> getAuctionPrice (
            @PathVariable Long id // auctionId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getAuctionPrice(id));
    }

}
