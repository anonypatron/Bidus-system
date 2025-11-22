package com.web.bff.controller;

import com.common.AuctionStatus;
import com.common.dto.user.UserPrincipal;
import com.web.bff.dto.auction.AuctionCreateRequestDto;
import com.web.bff.dto.auction.AuctionPriceDto;
import com.web.bff.dto.auction.AuctionResponseDto;
import com.web.bff.dto.auction.AuctionUpdateRequestDto;
import com.web.bff.service.AuctionBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auctions")
public class AuctionBffController {

    private final AuctionBffService auctionBffService;

    @PostMapping
    public Mono<ResponseEntity<AuctionResponseDto>> createAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestPart("auctionData") AuctionCreateRequestDto dto,
            @RequestPart("image") Mono<FilePart> image // [변경] MultipartFile -> Mono<FilePart>
    ) {
        return auctionBffService.save(userPrincipal.getId(), dto, image)
                .map(res -> ResponseEntity.status(HttpStatus.CREATED).body(res));
    }

    // 2. 특정 경매 검색
    @GetMapping("/{auctionId}")
    public Mono<ResponseEntity<AuctionResponseDto>> getAuction(
            @PathVariable Long auctionId
    ) {
        return auctionBffService.getAuction(auctionId)
                .map(ResponseEntity::ok);
    }

    // 3. 상태에 따른 페이지 객체
    @GetMapping
    public Mono<ResponseEntity<Page<AuctionResponseDto>>> getAuctions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "IN_PROGRESS") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Long userId = userPrincipal == null ? null : userPrincipal.getId();
        return auctionBffService.getAuctions(userId, status, PageRequest.of(page, size))
                .map(ResponseEntity::ok);
    }

    // 4. 사용자 경매 기록 조회
    @GetMapping("/history")
    public Mono<ResponseEntity<List<AuctionResponseDto>>> getHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(value = "role", defaultValue = "seller") String role,
            @RequestParam(value = "status", defaultValue = "CLOSED") AuctionStatus status
    ) {
        return auctionBffService.getAuctionHistory(userPrincipal.getId(), role, status)
                .map(ResponseEntity::ok);
    }

    // 5. 경매 수정
    @PatchMapping("/{auctionId}")
    public Mono<ResponseEntity<Void>> updateAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId,
            @RequestPart("auctionData") AuctionUpdateRequestDto dto,
            @RequestPart(value = "image", required = false) Mono<FilePart> image // [변경]
    ) {
        return auctionBffService.update(userPrincipal.getId(), auctionId, dto, image)
                .then(Mono.just(ResponseEntity.ok().build())); // 200 OK
    }

    // 6. 경매 삭제
    @DeleteMapping("/{auctionId}")
    public Mono<ResponseEntity<Void>> deleteAuction(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        return auctionBffService.delete(userPrincipal.getId(), auctionId)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build())); // 204 No Content
    }

    // 북마크한 경매 보여주기
    @GetMapping("/bookmark")
    public Mono<ResponseEntity<Page<AuctionResponseDto>>> getBookmarkedAuctions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "status", defaultValue = "IN_PROGRESS") AuctionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return auctionBffService.getBookmarkedAuctions(principal.getId(), status, PageRequest.of(page, size))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}/current-price")
    public Mono<ResponseEntity<AuctionPriceDto>> getAuctionPrice (
            @PathVariable Long id // auctionId
    ) {
        return auctionBffService.getAuctionPrice(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/current-bidding")
    public Mono<ResponseEntity<List<AuctionResponseDto>>> getCurrentBidding(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return auctionBffService.getAuctionCurrentBidding(principal.getId())
                .map(ResponseEntity::ok);
    }

}
