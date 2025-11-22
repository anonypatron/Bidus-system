package com.web.bff.controller;

import com.common.dto.user.UserPrincipal;
import com.web.bff.service.BookmarkBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkBffController {

    private final BookmarkBffService bookmarkBffService;

    @GetMapping
    public Mono<ResponseEntity<List<Long>>> getBookmarkedAuctionIds(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return bookmarkBffService.getBookmarkedAuctionIdsByUserId(userPrincipal.getId())
                .map(ResponseEntity::ok);
    }

    // 2. 북마크 추가 (POST)
    @PostMapping("/{auctionId}")
    public Mono<ResponseEntity<Void>> addBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        return bookmarkBffService.save(userPrincipal.getId(), auctionId)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping("/{auctionId}")
    public Mono<ResponseEntity<Void>> deleteBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        return bookmarkBffService.delete(userPrincipal.getId(), auctionId)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

}
