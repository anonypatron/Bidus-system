package com.auction.controller;

import com.auction.dto.request.AuctionCreateRequestDto;
import com.auction.dto.request.AuctionUpdateRequestDto;
import com.auction.dto.response.AuctionResponseDto;
import com.auction.service.AuctionService;
import com.common.AuctionStatus;
import com.common.dto.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
        AuctionResponseDto res = auctionService.save(userPrincipal.getId(), dto, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 특정 경매 검색
    // GET http://localhost/api/auctions/1
    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionResponseDto> getAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        AuctionResponseDto res = auctionService.getAuction(userId, auctionId);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 상태에 따른 페이지 객체
    // GET http://localhost/api/auctions?status=CLOSED&page=0&size=10
    @Deprecated
    @GetMapping
    public ResponseEntity<Page<AuctionResponseDto>> getAuctions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "IN_PROGRESS") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        Page<AuctionResponseDto> res = auctionService.getAuctions(userId, status, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    // 사용자의 경매(구매) 기록 조회 GET http://localhost/api/auctions/history?role=seller&staus=IN_PROGRESS
    // 사용자의 경매(판매) 기록 조회 GET http://localhost/api/auctions/history
    @Deprecated
    @GetMapping("/history")
    public ResponseEntity<List<AuctionResponseDto>> getHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "role", defaultValue = "seller") String role,
            @RequestParam(value = "status", defaultValue = "CLOSED") AuctionStatus status
    ) {
        List<AuctionResponseDto> history = auctionService.getAuctionHistory(userPrincipal.getId(), role, status);
        return ResponseEntity.status(HttpStatus.OK).body(history);
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

    // 북마크 등록
    // POST http://localhost/api/auctions/1/bookmark
    @PostMapping("/{auctionId}/bookmark")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        auctionService.addBookmark(userPrincipal.getId(), auctionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 북마크 삭제
    // DELETE http://localhost/api/auctions/1/bookmark
    @DeleteMapping("/{auctionId}/bookmark")
    public ResponseEntity<Void> deleteBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        auctionService.deleteBookmark(userPrincipal.getId(), auctionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 북마크되어있는 경매 확인
    // GET http://localhost/api/auctions/bookmark?status=IN_PROGRESS&page=0&size=10
    @GetMapping("/bookmark")
    public ResponseEntity<Page<AuctionResponseDto>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "IN_PROGRESS") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.getBookmarks(userId, status, PageRequest.of(page, size)));
    }

}
