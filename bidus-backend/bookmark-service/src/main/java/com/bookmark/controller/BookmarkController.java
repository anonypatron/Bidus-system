package com.bookmark.controller;

import com.bookmark.service.BookmarkService;
import com.common.dto.user.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 특정 사용자에 대한 북마크 정보
    @GetMapping
    public ResponseEntity<Set<Long>> getBookmarkedAuctionIds(
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Set<Long> auctionIds = bookmarkService.getBookmarkedAuctionIdsByUserId(userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(auctionIds);
    }

    @PostMapping("/{auctionId}")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        bookmarkService.save(userPrincipal.getId(), auctionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> deleteBookmark(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long auctionId
    ) {
        bookmarkService.delete(userPrincipal.getId(), auctionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
